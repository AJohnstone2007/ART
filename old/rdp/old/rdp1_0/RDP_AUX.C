/****************************************************************************
*
* RDP release 1.00 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 14 Feb 1994
*
* rdp_aux.c - rdp semantic routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include "crash.h"
#include "symbol.h"
#include "set.h"
#include "scan.h"
#include "rdp_aux.h"

static unsigned column = 0,
 set_width;											/* how many elements in set */

char *bnffilename = NULL,
*start_id = NULL,								/* identifier for first production: used as start symbol */
*primary_id;										/* identifier for parent production */
unsigned component;							/* sub-production component number */

char
*dir_title = NULL,							/* string from TITLE directive */
*dir_suffix = NULL,							/* string from SUFFIX directive */
*dir_pre_process = NULL,				/* string from PRE_PROCESS directive */
*dir_post_process = NULL,				/* string from POST_PROCESS directive */
*dir_output_file = NULL;				/* string from OUTPUT_FILE directive */

unsigned dir_set_size = 0,			/* number from SET_SIZE directive */
 dir_text_mode = 0,							/* TEXT_MODE flag */
 dir_case_insensitive = 0,			/* CASE_INSENSITIVE flag */
 dir_show_skips = 0,						/* SHOW_SKIPS flag */
 dir_newline_visible = 0,				/* NEWLINE_VISIBLE flag */
 dir_comments_visible = 0,			/* COMMENTS_VISIBLE flag */
 dir_hash_size = 101,						/* HASH_SIZE directive */
 dir_hash_prime = 31,						/* HASH_PRIME dirctive */
 dir_tab_width = 8,							/* TAB_WIDTH directive */
 dir_text_size = 8192,					/* TEXT_SIZE directive */
 dir_max_errors = 25,						/* MAX_ERRORS directive */
 dir_max_warnings = 100;				/* MAX_WARNINGS directive */

struct string_list
*dir_help,											/* strings from HELP directives */
*dir_include;										/* strings from INCLUDE directives */

set__
production_set,
empty_production_set;

symbol__ *find(char *id, unsigned kind, int must_be_new)
{
	symbol__ *temp;

	if ((temp = symbol__lookup_id(id)) == NULL)
	{
		temp = symbol__insert_id(id);
		temp->token = P_ID;
		temp->data = crash__calloc(sizeof(struct data_node), "symbol data");
		DATA(temp, kind) = kind;
		DATA(temp, first) = set__make();
		DATA(temp, first_cardinality) = 0;
		DATA(temp, follow) = set__construct(P_EOF, SET_END);
		DATA(temp, follow_cardinality) = 1;
		DATA(temp, return_type_stars) = 0;
		switch (kind)
		{
		case K_NUMBER_PRIMITIVE:
		case K_TOKEN:
			DATA(temp, return_type) = "int";
			break;
		case K_STRING_PRIMITIVE:
			DATA(temp, return_type) = "char";
			DATA(temp, return_type_stars) = 1;
			break;
		default:
			DATA(temp, return_type) = "void";
			break;
		}
	}
	else
		/* check to see if this production has already got a list */
	if (must_be_new && DATA(temp, been_defined))
		scan__error_echo("Doubly defined production '%s'", id);

	return temp;
}

void check_token_valid(char *id)
{
	if (*id == 0)
		scan__error_echo("Empty tokens are not allowed: use [..] instead");
	/* Test for embedded spaces in token */
	{
		char *c = id + 1;
		int bad = 0;

		while (*c != 0)
			bad |= isspace(*c++);
		if (bad)
			scan__error_echo("Tokens must not contain white space");
	}
}

void check_prod_name_valid(char *id)
{
	/* Test for two consecutive _ in token */
	char *c = id;
	int bad = 0;

	while (*c != 0)
		bad |= (*c++ == '_' && *c == '_');
	if (bad)
		scan__error_echo("Production names must not contain two consecutive underscores");
}

static int follow_changed;			/* repeat until done flag for follow sets */

/* Grammar checking routines */
static void first(symbol__ * prod)
{
	if (DATA(prod, in_use))				/* something has gone wrong */
	{
		scan__error("Production '%s' is left recursive", prod->id);	/* and return */
		return;
	}

	if (!DATA(prod, first_done))	/* something to do */
	{
		struct list_node *list = DATA(prod, list);	/* set up alternates pointer */

		DATA(prod, in_use) = 1;			/* mark this production as being processed */

		if (DATA(prod, kind) == K_SEQUENCE)	/* sequences are treated differently */
		{
			DATA(prod, contains_null) = 1;	/* set up list flag */
			while (list != NULL && DATA(prod, contains_null))	/* scan until non-empty alternate is found */
			{
				if (!DATA(list->production, first_done))	/* do first */
					first(list->production);

				set__unite_set(DATA(prod, first), DATA(list->production, first));	/* add alternate first set to production first set */
				DATA(prod, contains_null) = DATA(list->production, contains_null);	/* set contains_null flag */

				list = list->next;
			}
		}
		else
			while (list != NULL)			/* scan all alternates */
			{
				if (!DATA(list->production, first_done))	/* do first */
					first(list->production);

				set__unite_set(DATA(prod, first), DATA(list->production, first));	/* add alternate first set to production first set */
				DATA(prod, contains_null) |= DATA(list->production, contains_null);	/* OR in contains_null flag */
				list = list->next;
			}
		DATA(prod, in_use) = 0;			/* production is no longer in use */
		DATA(prod, first_done) = 1;	/* first set is now complete */
		/* and set cardinality */
		DATA(prod, first_cardinality) = set__cardinality(DATA(prod, first));
	}
}

/* scan a sequence production for follow set changes */
static void follow_sequence(symbol__ * prod)
{
	struct list_node *check = DATA(prod, list);	/* pointer to sequence list */

	while (check != NULL)					/* scan entire sequence and add to follow sets */
	{
		struct list_node *following = check;	/* temporary to look at followers */
		unsigned old_cardinality = DATA(check->production, follow_cardinality);

		do													/* scan up list adding first sets of trailing productions */
		{
			following = following->next;
			if (following == NULL)		/* We hit end of sequence */
				set__unite_set(DATA(check->production, follow), DATA(prod, follow));
			else
				set__unite_set(DATA(check->production, follow), DATA(following->production, first));
		}
		while (following != NULL && DATA(following->production, contains_null));

		/* Update cardinality changed flag */
		follow_changed |= ((DATA(check->production, follow_cardinality) =
												set__cardinality(DATA(check->production, follow))
												) != old_cardinality);

		check = check->next;				/* step to next item in sequence */
	}
}

/* scan an alternate for follow set changes */
static void follow_alternate(symbol__ * prod)
{
	struct list_node *check = DATA(prod, list);	/* pointer to alternate list */

	while (check != NULL)
	{
		unsigned old_cardinality = DATA(check->production, follow_cardinality);

		set__unite_set(DATA(check->production, follow), DATA(prod, follow));

		follow_changed |= ((DATA(check->production, follow_cardinality) =
												set__cardinality(DATA(check->production, follow))
												) != old_cardinality);
		check = check->next;
	}
}

static int bad_grammar(symbol__ * base)
{
	symbol__ *temp = base->next_scope;
	unsigned follow_pass = 0,
	 bad = 0;
	set__ work = set__make();

	/* Check for empties */
	if (verbose)
		scan__info("Checking for empty productions and alternates");

	while (temp != NULL)
	{
		struct list_node *list = DATA(temp, list);
		int k = DATA(temp, kind),
		 bad_alternate = 1;

		if (list == NULL && k == K_PRIMARY)
		{
			scan__warning("Production '%s' is empty", temp->id);
			bad = 1;
		}

		if (k == K_SEQUENCE)				/* check for empty alternates */
			while (list != NULL)
			{
				if (DATA(list->production, kind) != K_CODE)
					bad_alternate = 0;
				list = list->next;
			}
		else
			bad_alternate = 0;

		if (bad_alternate)
		{
			scan__error("Production '%s' has empty alternate or alternate containing only code", temp->id);
			bad = 1;
		}
		temp = temp->next_scope;
	}

	/* find first symbol__ sets */
	if (verbose)
		scan__info("Generating first sets");

	temp = base->next_scope;
	while (temp != NULL)
	{
		first(temp);
		temp = temp->next_scope;
	}

	/* add first() to follow() for iterations */
	temp = base->next_scope;
	while (temp != NULL)
	{
		if (DATA(temp, kind) == K_ITERATION)
		{
			set__unite_set(DATA(temp, follow), DATA(temp, first));
			DATA(temp, follow_cardinality) = set__cardinality(DATA(temp, follow));
		}
		temp = temp->next_scope;
	}

	/* find follow symbol__ sets */
	if (verbose)
		scan__info("Generating follow sets");

	do
	{
		follow_pass++;
		follow_changed = 0;
		temp = base->next_scope;
		while (temp != NULL)
		{
			if (DATA(temp, kind) == K_SEQUENCE)	/* only need to scan sequences */
				follow_sequence(temp);
			else
				follow_alternate(temp);
			temp = temp->next_scope;
		}
	}
	while (follow_changed);

	if (verbose)
		scan__info("Follow sets stabilised after %u pass%s", follow_pass, follow_pass == 1 ? "" : "es");
	/* check that for each production, all alternates have unique start tokens */
	if (verbose)
		scan__info("Checking for disjoint first sets");

	temp = base->next_scope;
	while (temp != NULL)
	{
		if (set__inc_element(production_set, DATA(temp, kind)) && DATA(temp, kind) != K_SEQUENCE)
		{
			struct list_node *left = DATA(temp, list);

			while (left != NULL)
			{
				struct list_node *right = left->next;

				while (right != NULL)
				{
					set__assign_set(work, DATA(left->production, first));
					set__intersect_set(work, DATA(right->production, first));

					if (set__cardinality(work) != 0)
					{
						scan__error("** Production '%s' alternates '%s' and '%s' have start terminals in common",
											 temp->id, left->production->id, right->production->id);
						bad = 1;
					}
					right = right->next;
				}
				left = left->next;
			}
		}
		temp = temp->next_scope;
	}

	/* check that first(a) - follow (a) is empty for nullable a */
	if (verbose)
		scan__info("Checking nullable productions");

	temp = base->next_scope;
	while (temp != NULL)
	{
		if (DATA(temp, contains_null))
		{
			set__assign_set(work, DATA(temp, first));
			set__intersect_set(work, DATA(temp, follow));
			if (set__cardinality(work) != 0 && DATA(temp, kind) != K_ITERATION)
			{
				scan__error("** Production '%s' contains null but first /\\ follow is not empty", temp->id);
				bad = 1;
			}
		}
		temp = temp->next_scope;
	}
	return bad;
}

/* Output line breaking support */
void check_break(FILE * f)
{
	if (column >= line_break)
	{
		fprintf(f, "\n");
		column = 0;
	}
}

/* Parser output routines */
int is_valid_C_id(char *s)
{
	int temp;

	temp = (isalpha(*s) || *s == '_');
	while (*++s != '\0')
		temp = temp && (isalnum(*s) || *s == '_');
	return (temp);
}

static void print_parser_production_name(FILE * f, symbol__ * n, char *open_comm, char *close_comm)
{
	switch (DATA(n, kind))
	{
 case K_CODE:
		column += fprintf(f, "\"%s\"", n->id + 1);
		break;
	case K_TOKEN:
		if (is_valid_C_id(n->id + 1))
			column += fprintf(f, "T_%s", n->id + 1);
		else
			column += fprintf(f, "T_%u %s %s %s", DATA(n, token_value), open_comm, n->id + 1, close_comm);
		break;

	case K_NUMBER_PRIMITIVE:
	case K_STRING_PRIMITIVE:
		column += fprintf(f, "P_%s", n->id);
		break;
	default:
		column += fprintf(f, "%s", n->id);
		break;
	}

}

static void print_parser_string(FILE * f, char *s)
{
	fprintf(f, "\"");
	while (*s != 0)
	{
		if (*s == '\"' || *s == '\\' || *s == '\'')
			fprintf(f, "\\");
		fprintf(f, "%c", *s++);
	}
	fprintf(f, "\"");

}

/* A hideously inefficient set printing routine to aid debugging: endless linear searches */
static void print_parser_token_set(FILE * f, set__ tokens, symbol__ * base)
{
	unsigned count;								/* present set element */
	int first = 1;

	for (count = 0; count < RDP_SET_SIZE; count++)
		if (set__inc_element(tokens, count))
		{
			symbol__ *p = base->next_scope;

			while (p != NULL && DATA(p, token_value) != count)
				p = p->next_scope;

			if (!first)
				fprintf(f, ", ");
			else
				first = 0;
			print_parser_production_name(f, p, "(", ")");
		}
}

static void print_bnf(char *bnffilename, symbol__ * base)
{
	FILE *bnffile;
	symbol__ *temp;

	if (verbose)
		scan__info("\nDumping expanded EBNF description to '%s'\n", bnffilename);

	if ((bnffile = fopen(bnffilename, "w")) == NULL)
		crash__("Can't open expanded BNF output file");

	fprintf(bnffile,
					"(****************************************************************************\n"
					"*\n"
					"* Expanded EBNF generated by RDP\n"
					"*\n"
					"****************************************************************************)\n\n");
	temp = base->next_scope;
	while (temp != NULL)
	{
		struct list_node *list = DATA(temp, list);
		int k = DATA(temp, kind),
		 count;

		if (k != K_CODE && k != K_STRING_PRIMITIVE && k != K_NUMBER_PRIMITIVE)
		{
			print_parser_production_name(bnffile, temp, "(*", "*)");
			fprintf(bnffile, ":%s", DATA(temp, return_type));
			for (count = 0; count < DATA(temp, return_type_stars); count++)
				fprintf(bnffile, "*");
			fprintf(bnffile, " ::= ");

			if (k == K_TOKEN)
				fprintf(bnffile, "'");
			fprintf(bnffile, "%s",
							k == K_TOKEN ? temp->id + 1 :
							k == K_NUMBER_PRIMITIVE || k == K_STRING_PRIMITIVE ? "(* Scanner primitive *) " :
							k == K_DO_FIRST ? "( " :
							k == K_LIST ? "< " :
							k == K_CONDITIONAL ? "[ " :
							k == K_ITERATION ? "{ " : "");

			while (list != NULL)
			{
				print_parser_production_name(bnffile, list->production, "(*", "*)");
				if (list->return_name != NULL)
					fprintf(bnffile, ":%s", list->return_name);
				fprintf(bnffile, " ");
				list = list->next;
				if (k != K_SEQUENCE && list != NULL)
					fprintf(bnffile, "| ");
			}

			fprintf(bnffile, "%s",
							k == K_TOKEN ? "'" :
							k == K_DO_FIRST ? ") " :
							k == K_LIST ? "> " :
							k == K_CONDITIONAL ? "] " :
							k == K_ITERATION ? "} " : "");

			if (k == K_LIST)
				fprintf(bnffile, "'%s'", DATA(temp, supplementary_token)->id + 1);

			fprintf(bnffile, ".\n");

			/* Now print first and follow sets */
			fprintf(bnffile, "(* First() = {%s", DATA(temp, contains_null) ? "(NULL) " : "");
			print_parser_token_set(bnffile, DATA(temp, first), base);
			fprintf(bnffile, "} *)\n");

			fprintf(bnffile, "(*  Stop() = {");
			print_parser_token_set(bnffile, DATA(temp, follow), base);
			fprintf(bnffile, "} *)\n");
			fprintf(bnffile, "\n");
		}
		temp = temp->next_scope;
	}
	fclose(bnffile);
}

static void print_header(char *headerfilename, symbol__ * base)
{
	FILE *headerfile;
	symbol__ *temp;

	if (verbose)
		scan__info("Dumping C parser header file to '%s'", headerfilename);

	if ((headerfile = fopen(headerfilename, "w")) == NULL)
		crash__("Can't open header output file");

	fprintf(headerfile,
					"/****************************************************************************\n"
					"*\n"
					"* Parser header file generated by RDP on " __DATE__ " at " __TIME__ "\n"
					"*\n"
					"****************************************************************************/\n");

	/* print token enumeration */
	column = 0;
	fprintf(headerfile, "\n/* Token enumeration */\nenum\n{\n");
	column += fprintf(headerfile, "TT_DUMMY = P_TOP");
	temp = base->next_scope;
	while (temp != NULL)
	{
		if (DATA(temp, kind) == K_TOKEN)
		{
			column += fprintf(headerfile, ", ");
			check_break(headerfile);
			print_parser_production_name(headerfile, temp, "/*", "*/");
		}
		temp = temp->next_scope;
	}
	fprintf(headerfile, ",\nTT_TOP\n};\n\n");

	fclose(headerfile);
}

static void print_parser_alternate(FILE * outputfile, symbol__ * production, symbol__ * primary);

static void print_parser_item(FILE * outputfile, symbol__ * prod, symbol__ * primary, char *return_name)
{
	switch (DATA(prod, kind))
	{
 case K_NUMBER_PRIMITIVE:
 case K_STRING_PRIMITIVE:
 case K_TOKEN:
		fprintf(outputfile, "scan__test(");
		if (return_name != NULL)
			fprintf(outputfile, "(%s = %s", return_name,
							DATA(prod, extended_class) != E_SIMPLE ||
							DATA(prod, kind) == K_STRING_PRIMITIVE ? "scan__sym->id," :
							DATA(prod, kind) == K_NUMBER_PRIMITIVE ? "scan__sym->number," : "");

		print_parser_production_name(outputfile, prod, "/*", "*/");
		if (return_name != NULL)
			fprintf(outputfile, ")");
		fprintf(outputfile, ", %s_stop, \"Expecting \"", primary->id);
		print_parser_string(outputfile, DATA(prod, kind) == K_TOKEN ? prod->id + 1 : prod->id);
		fprintf(outputfile, ");\n scan__();\n");
		break;
	case K_CODE:
		fprintf(outputfile, "%s\n", prod->id + 1);
		break;
	case K_PRIMARY:
		if (return_name != NULL)
			fprintf(outputfile, "%s = ", return_name);
		fprintf(outputfile, "%s();\n", prod->id);
		break;
	case K_SEQUENCE:
		crash__("Unexpected alternate");
		break;
	case K_DO_FIRST:
		fprintf(outputfile, "{\n");
		print_parser_alternate(outputfile, prod, primary);
		fprintf(outputfile, "}\n");
		break;
	case K_LIST:
		fprintf(outputfile, "{\n");
		print_parser_alternate(outputfile, prod, primary);
		fprintf(outputfile, "}\nwhile (scan__sym->token == ");
		print_parser_production_name(outputfile, DATA(prod, supplementary_token), "/*", "*/");
		fprintf(outputfile, ")\n{\nscan__(); /* skip delimiter */");
		print_parser_alternate(outputfile, prod, primary);
		fprintf(outputfile, "}\n");
		break;
	case K_CONDITIONAL:
		fprintf(outputfile, "if (set__inc_element(%s_first, scan__sym->token))\n{\n", prod->id);
		print_parser_alternate(outputfile, prod, primary);
		fprintf(outputfile, "}\n");
		break;
	case K_ITERATION:
		fprintf(outputfile, "while (set__inc_element(%s_first, scan__sym->token))\n{\n", prod->id);
		print_parser_alternate(outputfile, prod, primary);
		fprintf(outputfile, "}\n");
		break;
	}
}

static void print_parser_sequence(FILE * outputfile, symbol__ * production, symbol__ * primary)
{
	struct list_node *list = DATA(production, list);

	while (list != NULL)
	{
		print_parser_item(outputfile, list->production, primary, list->return_name);
		list = list->next;
	}
}

static void print_parser_alternate(FILE * outputfile, symbol__ * production, symbol__ * primary)
{
	struct list_node *list = DATA(production, list);

	if (list->next == NULL)				/* special case: only one alternate */
		print_parser_sequence(outputfile, list->production, primary);
	else
	{
		while (list != NULL)
		{
			if (DATA(list->production, kind) != K_SEQUENCE)
				crash__("Internal error: expecting alternate production");

			fprintf(outputfile, "if (set__inc_element(%s_first, scan__sym->token))\n{\n", list->production->id);
			print_parser_sequence(outputfile, list->production, primary);

			if ((list = list->next) != NULL)
				fprintf(outputfile, "}\nelse ");
			else
				fprintf(outputfile, "}\n");
		}
	}
}

static void print_collect_declarations(symbol__ * base, symbol__ * scope)
{
	struct list_node *list = DATA(base, list);

	while (list != NULL)
	{
		if (DATA(list->production, kind) != K_PRIMARY)
			print_collect_declarations(list->production, scope);
		if (list->return_name != NULL)
			/* insert symbol and point at parent data block */
			if (symbol__lookup_id_scope(list->return_name, scope) == NULL)
				symbol__insert_id(list->return_name)->data = list->production->data;
		list = list->next;
	}
}

static void print_parser_primaries(FILE * outputfile, symbol__ * base)
{
	symbol__ *temp = base->next_scope;

	fprintf(outputfile, "\n/* Parser functions */\n");
	while (temp != NULL)
	{
		if (DATA(temp, kind) == K_PRIMARY)
		{
			int count,
			 is_void = (strcmp(DATA(temp, return_type), "void") == 0);

			symbol__ *locals = symbol__new_scope(temp->id);

			fprintf(outputfile, "%s", DATA(temp, return_type));

			for (count = 0; count < DATA(temp, return_type_stars); count++)
				fprintf(outputfile, "*");
			fprintf(outputfile, " %s(void)\n{\n", temp->id);

			/* scan all subproductions and add return variables to symbol__ table */
			if (!is_void)
				symbol__insert_id("result")->data = temp->data;	/* loop back on ourselves */

			print_collect_declarations(temp, locals);

			symbol__scope_invisible(locals);
			if (locals->next_scope != NULL)
				do
				{
					locals = locals->next_scope;
					fprintf(outputfile, "%s", DATA(locals, return_type));
					for (count = 0; count < DATA(locals, return_type_stars); count++)
						fprintf(outputfile, "*");
					fprintf(outputfile, " %s;\n", locals->id);
				}
				while (locals->next_scope != NULL);

			fprintf(outputfile, "if (scan__test_set(%s_first, %s_stop, \"Expecting %s\"))\n"
							"{\n", temp->id, temp->id, temp->id);

			print_parser_alternate(outputfile, temp, temp);
			/* add error handling on exit */
			fprintf(outputfile, "scan__test_set(%s_stop, %s_stop, \"Expecting %s follow symbol\");\n"
							"}\n%s}\n\n",
							temp->id, temp->id, temp->id, is_void ? "" : "return result;\n");
		}
		temp = temp->next_scope;
	}
}

static void print_parser_include_line(FILE * outputfile, struct string_list * p)
{
	if (p->next != NULL)
		print_parser_include_line(outputfile, p->next);
	fprintf(outputfile, "#include \"%s\"\n", p->str1);
}

static void print_parser_help_line(FILE * outputfile, struct string_list * p)
{
	if (p->next != NULL)
		print_parser_help_line(outputfile, p->next);
	fprintf(outputfile, "      \"%s%s\\n\"\n", isspace(*(p->str1)) ? "" : "-", p->str1);
}

static void print_parser_switch_line(FILE * outputfile, struct string_list * p)
{
	if (p->next != NULL)
		print_parser_help_line(outputfile, p->next);
	if (!isspace(*(p->str1)))
		fprintf(outputfile, "     case '%c':\n       %s\n       break;\n", *(p->str1), p->str2);
}


static void print_parser(char *outputfilename, symbol__ * base)
{
	FILE *outputfile;
	symbol__ *temp;

	if (verbose)
		scan__info("Dumping C parser main file to '%s'", outputfilename);

	if ((outputfile = fopen(outputfilename, "w")) == NULL)
		crash__("Can't open parser output file");

	/* print main file header */
	fprintf(outputfile,
					"/****************************************************************************\n"
					"*\n"
					"* Parser generated by RDP on " __DATE__ " at " __TIME__ "\n"
					"*\n"
					"****************************************************************************/\n"
					"#include <stdio.h>\n"
					"#include \"crash.h\"\n"
					"#include \"symbol.h\"\n"
					"#include \"set.h\"\n"
					"#include \"scan.h\"\n"
					"#include \"%s\"\n", scan__add_filetype(outputfilename, "h"));

	if (dir_include != NULL)
		print_parser_include_line(outputfile, dir_include);

	fprintf(outputfile,
					"\nchar *sourcefilename = NULL,/* source file name */\n"
					"*outputfilename = \"%s\",		/* output file name */\n", dir_output_file);
	fprintf(outputfile,
					"verbose = 0;                   /* verbosity flag */\n\n"
					"void help(char *msg)\n"
					"{\n"
					"	crash__(\"%%s\\n\\n %s \" __DATE__ \" \" __TIME__ \"\\n\\n\"\n", dir_title);

	fprintf(outputfile,
					"                 \"Usage: %.*s [options] sourcefile \\n\\n\"\n"
					"                 \"Options:\\n\\n\"\n"
														"                 \"-F  Force creation of output files\\n\"\n"
					"		  \"-l  Make a listing\\n\"\n"
					"		  \"-ofilename Write output to filename\\n\"\n"
					"		  \"-s  Echo each scanner symbol as it is read\\n\"\n"
					"		  \"-S  Print summary symbol table statistics\\n\"\n"
					"		  \"-tn Tab expansion width (default %u)\\n\"\n"
					"		  \"-Tn Text buffer size in bytes for scanner (default %u)\\n\"\n"
					"		  \"-v  Set verbose mode\\n\"\n",
					(int) strcspn(outputfilename, "."), outputfilename, dir_tab_width, dir_text_size);

	if (dir_help != NULL)
		print_parser_help_line(outputfile, dir_help);

	fprintf(outputfile,
					"		  ,msg == NULL ? \"\" : msg);\n"
					"}\n");

	/* print load keyword function */
	column = 0;
	fprintf(outputfile, "\n/* Load keywords */\nvoid load_keywords(void)\n{\n");
	temp = base->next_scope;
	while (temp != NULL)
	{
		if (DATA(temp, kind) == K_TOKEN)
		{
			fprintf(outputfile, "  scan__load_%skeyword(",
							DATA(temp, extended_class) == E_SIMPLE ? "" : "extended_");
			print_parser_string(outputfile, temp->id + 1);
			fprintf(outputfile, ", ");
			print_parser_production_name(outputfile, temp, "/*", "*/");
			if (DATA(temp, extended_class) != E_SIMPLE)
			{
				fprintf(outputfile, ", %i, ", DATA(temp, extended_class));
				print_parser_string(outputfile, DATA(temp, close) + 1);
			}
			fprintf(outputfile, ");\n");
		}
		temp = temp->next_scope;
	}
	fprintf(outputfile, "}\n");

	/* print set declaration */
	fprintf(outputfile, "\n/* Set declarations */\n\nunsigned char");
	temp = base->next_scope;
	while (temp != NULL)
	{
		if (set__inc_element(production_set, DATA(temp, kind)))
		{
			int count;

			fprintf(outputfile, "\n  %s_first[%u] = {", temp->id, set_width);
			for (count = 0; count < set_width; count++)
				fprintf(outputfile, "%c%u", count == 0 ? ' ' : ',', DATA(temp, first)[count]);
			fprintf(outputfile, " }, ");

			if (DATA(temp, kind) == K_PRIMARY)
			{
				fprintf(outputfile, "\n  %s_stop[%u] = {", temp->id, set_width);
				for (count = 0; count < set_width; count++)
					fprintf(outputfile, "%c%u", count == 0 ? ' ' : ',', DATA(temp, follow)[count]);
				fprintf(outputfile, " }, ");
			}
		}
		temp = temp->next_scope;
	}
	fprintf(outputfile, "\nerror_sync;\n");

	/* print forward declarations */
	fprintf(outputfile, "\n/* Parser forward declarations */\n");
	temp = base->next_scope;
	while (temp != NULL)
	{
		if (DATA(temp, kind) == K_PRIMARY)
		{
			int count;

			fprintf(outputfile, "%s", DATA(temp, return_type));
			for (count = 0; count < DATA(temp, return_type_stars); count++)
				fprintf(outputfile, "*");
			fprintf(outputfile, " %s(void);\n", temp->id);
		}
		temp = temp->next_scope;
	}

	/* print parser definitions */
	print_parser_primaries(outputfile, base);

	/* print main line routine */
	fprintf(outputfile,
					"int main(int argc, char *argv[])\n"
					"{\n"
					" int tabwidth = 4,			/* tab expansion width */\n"
					" textsize = 8192,			/* size of scanner text array */\n"
					" force = 0,            /* force output files flag */\n"
					" symbol__statistics = 0;		/* show symbol__ table statistics flag */\n"
					" symbol__ *base;         /* base of parser production scope */\n"
					" while (--argc > 0)\n"
					" {\n"
					"	 if ((*++argv)[0] == '-')/* switch */\n"
					"	 {\n"
					"		 switch ((*argv)[1])\n"
					"		 {\n"
					"		 case 'o':\n"
					"			 outputfilename = *argv + 2;\n"
					"			 break;\n"
					"	 	 case 'S':\n"
					"		 	symbol__statistics = 1;\n"
					"		 	break;\n"
					"		 case 's':\n"
					"			 scan__symbol_echo(1);\n"
					"			 break;\n"
					"		 case 'l':\n"
					"			 scan__line_echo(1);\n"
					"			 break;\n"
					"		 case 't':\n"
					"			 sscanf(*argv + 2, \"%%u\", &tabwidth);\n"
					"			 scan__line_echo(tabwidth);\n"
					"			 break;\n"
					"	   case 'T':\n"
					"			 sscanf(*argv + 2, \"%%u\", &textsize);\n"
					"		   break;\n"
					"    case 'v':\n"
					"			 verbose = 1;\n"
					"			 break;\n");

	if (dir_help != NULL)
		print_parser_switch_line(outputfile, dir_help);

	fprintf(outputfile,
					"		 default:\n"
					"			 printf(\"\\nUnrecognised option -%%c\", (*argv)[1]);\n"
					"			 help(\"\");\n"
					"		 }\n"
					"	 }\n"
					"	 else\n"
					"		 sourcefilename = *argv;\n"
					" }\n");
	fprintf(outputfile,
					" if (sourcefilename == NULL)\n"
					"	 help(\"No source file specified\");\n");

	fprintf(outputfile,
					" set__init(%u);\n"
					" symbol__init(%u, %u);\n", dir_set_size, dir_hash_size, dir_hash_prime);

	fprintf(outputfile,
					" load_keywords();\n"
					" base = symbol__new_scope(\"parser\");	/* create bnf scope */\n"
					" scan__tab_width(%u);\n"
					" scan__newline_visible(%u);\n"
					" scan__comments_visible(%u);\n"
					" scan__case_insensitive(%u);\n"
					" scan__show_skips(%u);\n"
					" scan__summarise_errors(verbose);\n"
					" scan__init(%u,%u,%u);\n",
					dir_tab_width, dir_newline_visible, dir_comments_visible,
					dir_case_insensitive, dir_show_skips, dir_text_size,
					dir_max_errors, dir_max_warnings);

	if (dir_text_mode)
		fprintf(outputfile,
						"if (scan__open_text(\"Text mode\",\n"
						"                   scan__load_file(scan__default_filetype(sourcefilename, \"%s\"))\n"
						"                  ) == NULL)\n"
						"  help(\"Can't open source file\");\n",
						dir_suffix);
	else
		fprintf(outputfile,
						" if (scan__open_file(scan__default_filetype(sourcefilename, \"%s\")) == NULL)\n"
						"   help(\"Can't open source file\");\n",
						dir_suffix);

	fprintf(outputfile,
					" if (verbose)\n"
					"	 printf(\"\\n%s \" __DATE__ \" \" __TIME__ \"\\n\");\n", dir_title);

	if (dir_pre_process != NULL)
		fprintf(outputfile, " %s\n", dir_pre_process);

	fprintf(outputfile, " %s", start_id);
	fprintf(outputfile,
					"();						/* call parser at top level */\n"
					" if (symbol__statistics)\n"
					"  symbol__print_statistics();\n");

	fprintf(outputfile, " return %s\n}\n", dir_post_process == NULL ? "0;" : dir_post_process);

	fclose(outputfile);
}

void pre_process(void)
{
	production_set = set__construct(K_PRIMARY, K_SEQUENCE, K_DO_FIRST, K_LIST, K_CONDITIONAL, K_ITERATION, SET_END);
	empty_production_set = set__construct(K_LIST, K_CONDITIONAL, K_ITERATION, SET_END);

	DATA(find("ID", K_STRING_PRIMITIVE, 0), token_value) = P_ID;	/* add predefined primitive productions */
	DATA(find("NUMBER", K_NUMBER_PRIMITIVE, 0), token_value) = P_NUMBER;
	DATA(find("NEW_ID", K_STRING_PRIMITIVE, 0), token_value) = P_NEW_ID;
	DATA(find("EOLN", K_NUMBER_PRIMITIVE, 0), token_value) = P_EOLN;
	DATA(find("EOF", K_NUMBER_PRIMITIVE, 0), token_value) = P_EOF;
}

static void order_tokens(symbol__ * base)
{
	unsigned
	 token_count = P_TOP + 1;
	symbol__ *temp = base->next_scope;

	while (temp != NULL)
	{
		if (DATA(temp, kind) == K_TOKEN)
		{
			DATA(temp, token_value) = token_count++;
			if (token_count >= SET_END)
				crash__("Too many tokens: recompile RDP with a larger set size");
		}
		temp = temp->next_scope;
	}

	/* now set up start sets for tokens, code and primitives */
	temp = base->next_scope;
	while (temp != NULL)
	{
		if (DATA(temp, kind) == K_TOKEN || DATA(temp, kind) == K_NUMBER_PRIMITIVE ||
				DATA(temp, kind) == K_STRING_PRIMITIVE)
		{
			set__unite_element(DATA(temp, first), DATA(temp, token_value));
			DATA(temp, first_cardinality) = set__cardinality(DATA(temp, first));
			set__unite_element(DATA(temp, follow), P_EOF);
			DATA(temp, follow_cardinality) = set__cardinality(DATA(temp, follow));

			DATA(temp, first_done) = 1;
		}
		else if (DATA(temp, kind) == K_LIST)
		{
			set__unite_element(DATA(temp, follow), DATA(DATA(temp, supplementary_token), token_value));
			DATA(temp, follow_cardinality) = set__cardinality(DATA(temp, follow));
		}
		temp = temp->next_scope;
	}

	set_width = (token_count >> 3) + 1;
}

static void add_continuations(symbol__ * base)
{
	symbol__ *temp = base->next_scope;
	char *last_token = "";				/* remember most recent token name */

	if (verbose)
		scan__info("Checking for continuation tokens");

	while (temp != NULL)					/* scan over all productions */
	{
		if (DATA(temp, kind) == K_TOKEN)
		{
			char *lo = last_token,
			*hi = temp->id;

			if (*lo != 0 && !is_valid_C_id(hi + 1))
			{
				/* find common prefix */
				while (*lo == *hi && *hi != 0)	/* bump while they are identical */
				{
					lo++;
					hi++;
				}

				hi++;										/* we can't have two identical tokens, so at worst this will move to a null */

				/* now add continuations */
				/* is the first non-identical character the one before the terminating null ? */
				while (*(hi) != 0)			/* add a continuation */
				{
					/* insert the sub-string */
					char *c = temp->id,
					*continuation_name = scan__text;	/* remember start position */

					while (c != hi)
						scan__insert_char(*c++);	/* copy identifier */
					scan__insert_char(0);	/* add a terminating null */

					if (verbose)
						scan__info("Adding continuation token '%s'", continuation_name + 1);

					find(continuation_name, K_TOKEN, 0);

					hi++;
				}
			}

			last_token = temp->id;
		}
		temp = temp->next_scope;
	}
}

int post_process(char *outputfilename, int force, symbol__ * base)
{
	if (verbose)
		scan__info("");							/* skip a line */
	symbol__sort_scope(base);			/* sort productions into alphabetical order */
	add_continuations(base);			/* scan through tokens and add any necessry continuations */
	symbol__sort_scope(base);			/* re-sort productions into alphabetical order */
	order_tokens(base);						/* apply token numbers to token productions */
	bad_grammar(base);						/* find the non-LL(1)'isms */

	if (verbose)									/* see how much text buffer space we used */
		scan__print_statistics();

	if (scan__print_total_errors(verbose) && !force)
		crash__("Run aborted without creating output files: rerun with -F to override");

	if (bnffilename != NULL)
		print_bnf(scan__default_filetype(bnffilename, "bnf"), base);	/* dump bnf file */
	print_header(scan__add_filetype(outputfilename, "h"), base);	/* dump header file */
	print_parser(scan__add_filetype(outputfilename, "c"), base);	/* dump main file */
	return 0;
}

/* End of rdp_aux.c */
