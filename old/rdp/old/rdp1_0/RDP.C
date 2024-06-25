/****************************************************************************
*
* RDP release 1.00 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 14 Feb 1994
*
* rdp.c - hand written rdp front end
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#include <ctype.h>
#include "crash.h"
#include "symbol.h"
#include "set.h"
#include "scan.h"
#include "rdp.h"
#include "rdp_aux.h"

extern unsigned _stklen = 32000;

unsigned force = 0;							/* Force output files flag */
char *sourcefilename = NULL,		/* source file name */
*outputfilename = "rdparser",		/* output file name */
 verbose = 0;										/* verbose mode flag */

set__ error_sync,									/* error synchronisation tokens */
 unit_first,
 dir_first,
 dir_stop,
 prod_first,
 alt_first,
 seq_first,
 item_first,
 item_ret_first,
 item_inl_first,
 unit_stop,
 prod_stop,
 alt_stop,
 seq_stop,
 item_inl_stop,
 item_ret_stop,
 id_set;

void help(char *msg)
{
	crash__("%s\n\nRecursive descent parser generator V1.00 " __DATE__ " " __TIME__ "\n(c) Adrian Johnstone 1993\n\n"
				"Usage: rdp [options] source[.bnf] \n\n"
				"Options:\n\n"
				"-bfilename Write expanded BNF to filename\n"
				"-l  Make a listing\n"
				"-ofilename Write output to filename (default rdparser.c)\n"
				"-s  Echo each scanner symbol as it is read\n"
				"-S  Print summary symbol table statistics\n"
				"-tn Tab expansion width (default 2)\n"
				"-Tn Text buffer size in bytes for scanner (default 8192)\n"
				"-F  Force creation of output files\n"
				"-v  Set verbose mode\n"
				"\nYou can contact the author (Adrian Johnstone) at:\n\n"
				"Computer Science Department, Royal Holloway, University of London,\n"
				"Egham, Surrey, TW20 0EX UK. Email: adrian@dcs.rhbnc.ac.uk\n",
				msg == NULL ? "" : msg);
}

void load_keywords(void)				/* install keywords in symbol table */
{
	scan__load_keyword("HELP", T_HELP);
	scan__load_keyword("TITLE", T_TITLE);
	scan__load_keyword("SUFFIX", T_SUFFIX);
	scan__load_keyword("CASE_INSENSITIVE", T_CASE_INSENSITIVE);
	scan__load_keyword("SHOW_SKIPS", T_SHOW_SKIPS);
	scan__load_keyword("SET_SIZE", T_SET_SIZE);
	scan__load_keyword("HASH_SIZE", T_HASH_SIZE);
	scan__load_keyword("HASH_PRIME", T_HASH_PRIME);
	scan__load_keyword("TEXT_SIZE", T_TEXT_SIZE);
	scan__load_keyword("MAX_ERRORS", T_MAX_ERRORS);
	scan__load_keyword("MAX_WARNINGS", T_MAX_WARNINGS);
	scan__load_keyword("TAB_WIDTH", T_TAB_WIDTH);
	scan__load_keyword("TEXT_MODE", T_TEXT_MODE);
	scan__load_keyword("PRE_PROCESS", T_PRE_PROCESS);
	scan__load_keyword("POST_PROCESS", T_POST_PROCESS);
	scan__load_keyword("USES", T_USES);
	scan__load_keyword("OUTPUT_FILE", T_OUTPUT_FILE);
	scan__load_keyword("INCLUDE", T_INCLUDE);
	scan__load_keyword("NEWLINE_VISIBLE", T_NEWLINE_VISIBLE);
	scan__load_keyword("COMMENTS_VISIBLE", T_COMMENTS_VISIBLE);
	scan__load_keyword("STRING", T_STRING);
	scan__load_keyword("STRING_ESC", T_STRING_ESC);
	scan__load_keyword("COMMENT", T_COMMENT);
	scan__load_keyword("COMMENT_NEST", T_COMMENT_NEST);
	scan__load_keyword("{", T_LBRACE);
	scan__load_keyword("}", T_RBRACE);
	scan__load_keyword("(", T_LPAR);
	scan__load_keyword(")", T_RPAR);
	scan__load_keyword("[", T_LBRACK);
	scan__load_keyword("]", T_RBRACK);
	scan__load_keyword("<", T_LANGLE);
	scan__load_keyword(">", T_RANGLE);
	scan__load_keyword(".", T_PERIOD);
	scan__load_keyword("|", T_ALT);
	scan__load_keyword("::=", T_IS);
	scan__load_keyword(":", T_COLON);
	scan__load_keyword(",", T_COMMA);
	scan__load_keyword("*", T_STAR);
	scan__load_keyword("::", T_DCOLON);
	scan__load_extended_keyword("'", T_SQUOTE, E_STRING_ESC, "\\");
	scan__load_extended_keyword("\"", T_DQUOTE, E_STRING_ESC, "\\");
	scan__load_extended_keyword("(*", T_OCOMMENT, E_COMMENT_NEST, "*)");
}

void load_sets(void)						/* initisliase first and stop sets for each production */
{
	error_sync = set__construct(T_PERIOD, P_EOF, SET_END);
	unit_first = set__construct(P_ID, P_NEW_ID, T_HELP, T_TITLE, T_SUFFIX,
														 T_CASE_INSENSITIVE, T_SHOW_SKIPS,
														 T_SET_SIZE, T_PRE_PROCESS, T_POST_PROCESS,
                             T_HASH_SIZE, T_HASH_PRIME, T_TEXT_MODE,
														 T_TEXT_SIZE, T_TAB_WIDTH, T_MAX_ERRORS,
                             T_MAX_WARNINGS, T_USES, T_OUTPUT_FILE,
                             T_INCLUDE,
														 T_NEWLINE_VISIBLE, T_COMMENTS_VISIBLE, SET_END);
	prod_first = set__construct(P_ID, P_NEW_ID, SET_END);
	dir_first = set__construct(T_HELP, T_TITLE, T_SUFFIX,
														T_CASE_INSENSITIVE, T_SHOW_SKIPS,
													  T_SET_SIZE, T_PRE_PROCESS, T_POST_PROCESS,
														T_HASH_SIZE, T_HASH_PRIME, T_TEXT_MODE,
                            T_TEXT_SIZE, T_TAB_WIDTH, T_MAX_ERRORS,
                            T_MAX_WARNINGS, T_USES, T_OUTPUT_FILE,
                            T_INCLUDE,
														T_NEWLINE_VISIBLE, T_COMMENTS_VISIBLE, SET_END);
	alt_first = set__construct(P_ID, P_NEW_ID, T_SQUOTE, T_DQUOTE,
														T_STRING, T_STRING_ESC, T_COMMENT,
                            T_COMMENT_NEST, T_LBRACE, T_LBRACK, T_LPAR,
                            T_LANGLE,
														SET_END);

	seq_first = set__construct(P_ID, P_NEW_ID, T_SQUOTE, T_DQUOTE,
														T_STRING, T_STRING_ESC, T_COMMENT, T_COMMENT_NEST,
														T_LBRACE, T_LBRACK, T_LPAR, T_LANGLE,
														SET_END);

	item_first = set__construct(P_ID, P_NEW_ID, T_SQUOTE, T_DQUOTE,
														 T_STRING, T_STRING_ESC, T_COMMENT, T_COMMENT_NEST,
														 T_LBRACE, T_LBRACK, T_LPAR, T_LANGLE,
														 SET_END);
	item_inl_first = set__construct(T_DQUOTE,
																 T_LBRACE, T_LBRACK, T_LPAR, T_LANGLE,
																 SET_END);
	item_ret_first = set__construct(P_ID, P_NEW_ID, T_SQUOTE,
																 T_STRING, T_STRING_ESC, T_COMMENT, T_COMMENT_NEST,
																 SET_END);

	unit_stop = set__construct(P_EOF, SET_END);
	prod_stop = set__construct(P_ID, P_NEW_ID, T_HELP, T_TITLE, T_SUFFIX,
														T_CASE_INSENSITIVE, T_SHOW_SKIPS,
														T_SET_SIZE, T_PRE_PROCESS, T_POST_PROCESS,
                            T_HASH_SIZE, T_HASH_PRIME, T_TEXT_MODE,
                            T_TEXT_SIZE, T_TAB_WIDTH, T_MAX_ERRORS,
                            T_MAX_WARNINGS, T_USES, T_OUTPUT_FILE,
														T_INCLUDE,
														T_NEWLINE_VISIBLE, T_COMMENTS_VISIBLE,
														P_EOF, SET_END);
	dir_stop = set__construct(P_ID, P_NEW_ID, T_HELP, T_TITLE, T_SUFFIX,
													 T_CASE_INSENSITIVE, T_SHOW_SKIPS,
													 T_SET_SIZE, T_PRE_PROCESS, T_POST_PROCESS,
                           T_HASH_SIZE, T_HASH_PRIME, T_TEXT_MODE,
                           T_TEXT_SIZE, T_TAB_WIDTH, T_MAX_ERRORS,
                           T_MAX_WARNINGS, T_USES, T_OUTPUT_FILE,
                           T_INCLUDE,
													 T_NEWLINE_VISIBLE, T_COMMENTS_VISIBLE,
													 P_EOF, SET_END);
	alt_stop = set__construct(T_RBRACE, T_RBRACK, T_RPAR, T_RANGLE,
													 T_PERIOD, P_EOF, SET_END);
	seq_stop = set__construct(P_ID, P_NEW_ID, T_SQUOTE, T_DQUOTE,
													 T_LBRACE, T_LBRACK, T_LPAR, T_LANGLE,
													 T_RBRACE, T_RBRACK, T_RPAR, T_RANGLE,
													 T_ALT, T_PERIOD, P_EOF, SET_END);
	item_inl_stop = set__construct(P_ID, P_NEW_ID, T_SQUOTE, T_DQUOTE,
																T_STRING, T_STRING_ESC, T_COMMENT,
																T_COMMENT_NEST,
																T_LBRACE, T_LBRACK, T_LPAR, T_LANGLE,
																T_RBRACE, T_RBRACK, T_RPAR, T_RANGLE,
																T_ALT, T_PERIOD, T_COLON, P_EOF, SET_END);
	item_ret_stop = set__construct(P_ID, P_NEW_ID, T_SQUOTE, T_DQUOTE,
																T_STRING, T_STRING_ESC, T_COMMENT,
																T_COMMENT_NEST,
																T_LBRACE, T_LBRACK, T_LPAR, T_LANGLE,
																T_RBRACE, T_RBRACK, T_RPAR, T_RANGLE,
																T_ALT, T_PERIOD, T_COLON, P_EOF, SET_END);
	id_set = set__construct(P_ID, P_NEW_ID, SET_END);
}

/************** EBNF parser ***************************************/
list *alt(void);								/* forward definition */

symbol__ *item_ret(void)					/* parse a returnable item and return a symbol */
{
	symbol__*ret = NULL;

	if (scan__test_set(item_ret_first, item_ret_stop, "Expecting a returnable item"))
	{
		switch (scan__sym->token)
		{
		case P_ID:									/* Primary */
		case P_NEW_ID:
			scan__sym->token = P_ID;		/* Make NEW_ID's ID's to save text space */
			ret = find(scan__sym->id, K_PRIMARY, 0);
			scan__();
			break;

		case T_SQUOTE:							/* Token */
			check_token_valid(scan__sym->id);
			ret = find(scan__sym->id, K_TOKEN, 0);
			scan__();
			break;

		case T_STRING:
			scan__();
			scan__test(T_LPAR, prod_stop, "Expecting (");
			scan__();
			scan__test(T_SQUOTE, prod_stop, "Expecting string delimiter token");
			check_token_valid(scan__sym->id);
			ret = find(scan__sym->id, K_TOKEN, 0);
			DATA(ret, extended_class) = E_STRING;
			DATA(ret, close) = scan__sym->id;
			DATA(ret, return_type) = "char";
			DATA(ret, return_type_stars) = 1;
			scan__();
			scan__test(T_RPAR, prod_stop, "Expecting )");
			scan__();
			break;

		case T_STRING_ESC:
			scan__();
			scan__test(T_LPAR, prod_stop, "Expecting (");
			scan__();
			scan__test(T_SQUOTE, prod_stop, "Expecting string delimiter token");
			check_token_valid(scan__sym->id);
			ret = find(scan__sym->id, K_TOKEN, 0);
			scan__();
			scan__test(T_SQUOTE, prod_stop, "Expecting string escape token");
			check_token_valid(scan__sym->id);
			DATA(ret, extended_class) = E_STRING_ESC;
			DATA(ret, close) = scan__sym->id;
			DATA(ret, return_type) = "char";
			DATA(ret, return_type_stars) = 1;
			scan__();
			scan__test(T_RPAR, prod_stop, "Expecting )");
			scan__();
			break;

		case T_COMMENT:
			scan__();
			scan__test(T_LPAR, prod_stop, "Expecting (");
			scan__();
			scan__test(T_SQUOTE, prod_stop, "Expecting comment opening token");
			check_token_valid(scan__sym->id);
			ret = find(scan__sym->id, K_TOKEN, 0);
			scan__();
			scan__test(T_SQUOTE, prod_stop, "Expecting comment closing token");
			check_token_valid(scan__sym->id);
			DATA(ret, extended_class) = E_COMMENT;
			DATA(ret, close) = scan__sym->id;
			DATA(ret, return_type) = "char";
			DATA(ret, return_type_stars) = 1;
			scan__();
			scan__test(T_RPAR, prod_stop, "Expecting )");
			scan__();
			break;

		case T_COMMENT_NEST:
			scan__();
			scan__test(T_LPAR, prod_stop, "Expecting (");
			scan__();
			scan__test(T_SQUOTE, prod_stop, "Expecting comment opening token");
			check_token_valid(scan__sym->id);
			ret = find(scan__sym->id, K_TOKEN, 0);
			scan__();
			scan__test(T_SQUOTE, prod_stop, "Expecting comment closing token");
			check_token_valid(scan__sym->id);
			DATA(ret, extended_class) = E_COMMENT_NEST;
			DATA(ret, close) = scan__sym->id;
			DATA(ret, return_type) = "char";
			DATA(ret, return_type_stars) = 1;
			scan__();
			scan__test(T_RPAR, prod_stop, "Expecting )");
			scan__();
			break;
		}
		scan__test_set(item_ret_stop, item_ret_stop, "Expecting an item follow");
	}
	return ret;
}

symbol__*item_inl(void)					/* parse an inline item and return a symbol */
{
	symbol__*ret = NULL;

	if (scan__test_set(item_inl_first, item_inl_stop, "Expecting an inline item"))
	{
		switch (scan__sym->token)
		{
		case T_DQUOTE:							/* Code */
			ret = find(scan__sym->id, K_CODE, 0);
			DATA(ret, contains_null) = 1;
			scan__();
			break;

		case T_LPAR:								/* Do-first */
			scan__();										/* skip left parenthesis */
			ret = find(scan__insert_subid(primary_id, component++), K_DO_FIRST, 1);
			DATA(ret, list) = alt();	/* parse contents */
			scan__test(T_RPAR, item_inl_stop, "Expecting ) at end of do-first");
			scan__();
			break;

		case T_LBRACE:							/* Iteration */
			scan__();										/* skip left brace */
			ret = find(scan__insert_subid(primary_id, component++), K_ITERATION, 1);
			DATA(ret, list) = alt();	/* parse contents */
			DATA(ret, contains_null) = 1;
			scan__test(T_RBRACE, item_inl_stop, "Expecting } at end of iteration");
			scan__();
			break;

		case T_LBRACK:							/* Conditional */
			scan__();										/* skip left bracket */
			ret = find(scan__insert_subid(primary_id, component++), K_CONDITIONAL, 1);
			DATA(ret, list) = alt();	/* parse contents */
			DATA(ret, contains_null) = 1;
			scan__test(T_RBRACK, item_inl_stop, "Expecting ] at end of conditional");
			scan__();
			break;

		case T_LANGLE:							/* List */
			scan__();										/* skip left brace */
			ret = find(scan__insert_subid(primary_id, component++), K_LIST, 1);
			DATA(ret, list) = alt();	/* parse contents */
			scan__test(T_RANGLE, item_inl_stop, "Expecting > at end of list");
			scan__();
			scan__test(T_SQUOTE, item_inl_stop, "A list must be followed by a token");
			if (*(scan__sym->id + 1) == 0)
				scan__error_echo("Empty tokens are not allowed in lists");
			DATA(ret, supplementary_token) = find(scan__sym->id, K_TOKEN, 0);
			scan__();
			break;
		}
		scan__test_set(item_inl_stop, item_inl_stop, "Expecting an item follow");
	}
	return ret;
}


list *seq(void)									/* parse a sequence and return a list */
{
	struct list_node *base = crash__calloc(sizeof(struct list_node), "sequence list node"),	/* allocate dummy first element */
	*end = base;

	if (scan__test_set(seq_first, seq_stop, "Expecting a sequence"))
	{
		do
		{
			/* link in at end of sequence list */
			end->next = crash__calloc(sizeof(struct list_node), "sequence list node");
			end = end->next;

			if (set__inc_element(item_ret_first, scan__sym->token))
			{
				end->production = item_ret();
				if (scan__sym->token == T_COLON)	/* we have a return name */
				{
					scan__();								/* skip trailing colon */
					scan__test_set(id_set, prod_stop, "Expecting an identifier");
					end->return_name = scan__sym->id;
					scan__();
				}
			}
			else if (set__inc_element(item_inl_first, scan__sym->token))
				end->production = item_inl();
		}
		while (set__inc_element(item_first, scan__sym->token));

		scan__test_set(seq_stop, seq_stop, "Expecting a sequence follow");
	}
	return base->next;						/* drop sentinel */
}

list *alt(void)									/* parse and return a list of alternates */
{
	struct list_node *base,
	*end;

	/* make a dummy list node as a sentinel */
	base = crash__calloc(sizeof(struct list_node), "alt list node");
	end = base;

	if (scan__test_set(alt_first, alt_stop, "Expecting an alternate"))
	{
		do
		{
			/* allocate a new list node */
			list *body = seq();

			end->next = crash__calloc(sizeof(struct list_node), "alt list node");
			end = end->next;

			end->production = find(scan__insert_subid(primary_id, component++), K_SEQUENCE, 1);
			DATA(end->production, list) = body;
		}
		while (scan__sym->token == T_ALT ? scan__(), 1 : 0);

		scan__test_set(alt_stop, alt_stop, "Expecting an alternate follow");
	}

	return base->next;
}

void prod(void)									/* parse a production */
{
	if (scan__test_set(prod_first, prod_stop, "Expecting a production"))
	{
		{
			char *this_id = scan__sym->id,
			*return_type_name = "void";
			unsigned return_type_stars = 0;
			symbol__*body;

			check_prod_name_valid(this_id);	/* look for __ */

			if (start_id == NULL)
				start_id = this_id;

			primary_id = scan__sym->id;
			component = 0;


			scan__();
			if (scan__sym->token == T_COLON)
			{
				scan__();									/* skip trailing colon */
				scan__test_set(id_set, prod_stop, "Expecting an identifier");
				return_type_name = scan__sym->id;
				scan__();
				/* now collect star operators */
				while (scan__sym->token == T_STAR)
				{
					return_type_stars++;
					scan__();
				}
			}

			scan__test(T_IS, prod_stop, "A production definition must start with an ::= symbol");
			scan__();
			body = find(primary_id, K_PRIMARY, 0);
			DATA(body, list) = alt();	/* parse contents */
			DATA(body, been_defined) = 1;
			DATA(body, return_type) = return_type_name;
			DATA(body, return_type_stars) = return_type_stars;

			scan__test(T_PERIOD, prod_stop, "Expecting '.' at end of production");
			scan__();
		}
		scan__test_set(prod_stop, prod_stop, "Expecting a production follow");
	}
}

void dir(void)
{
	switch (scan__sym->token)
	{
		struct string_list *temp;
		char *filename;

	case T_HELP:
		scan__();
		scan__test(T_LPAR, dir_stop, "Expecting " "(");
		scan__();
		scan__test(T_DQUOTE, prod_stop, "Expecting code item");
		temp = crash__malloc(sizeof(struct string_list), "HELP node allocation");
		temp->str1 = scan__sym->id + 1;
		temp->next = dir_help;
		dir_help = temp;
		scan__();
		scan__test(T_DQUOTE, prod_stop, "Expecting code item");
		temp->str2 = scan__sym->id + 1;
		scan__();
		scan__test(T_RPAR, dir_stop, "Expecting " ")");
		scan__();
		break;

	case T_USES:
		scan__();
		scan__test(T_LPAR, dir_stop, "Expecting " "(");
		scan__();
		scan__test(T_DQUOTE, prod_stop, "Expecting code item");
		temp = crash__malloc(sizeof(struct string_list), "INCLUDE node allocation");
		temp->str1 = scan__sym->id + 1;
		temp->next = dir_include;
		dir_include = temp;
		scan__();
		scan__test(T_RPAR, dir_stop, "Expecting " ")");
		scan__();
		break;

	case T_INCLUDE:
		scan__();
		scan__test(T_LPAR, dir_stop, "Expecting " "(");
		scan__();
		scan__test(T_DQUOTE, prod_stop, "Expecting code item");
		filename = scan__sym->id + 1;
		scan__();
		scan__test(T_RPAR, dir_stop, "Expecting " ")");
		scan__();
		if (scan__open_file(filename) == NULL)
			scan__error_echo("Include file '%s' not found", filename);
		break;

	case T_PRE_PROCESS:
		scan__();
		scan__test(T_LPAR, dir_stop, "Expecting " "(");
		scan__();
		scan__test(T_DQUOTE, prod_stop, "Expecting code item");
		dir_pre_process = scan__sym->id + 1;
		scan__();
		scan__test(T_RPAR, dir_stop, "Expecting " ")");
		scan__();
		break;

	case T_POST_PROCESS:
		scan__();
		scan__test(T_LPAR, dir_stop, "Expecting " "(");
		scan__();
		scan__test(T_DQUOTE, prod_stop, "Expecting code item");
		dir_post_process = scan__sym->id + 1;
		scan__();
		scan__test(T_RPAR, dir_stop, "Expecting " ")");
		scan__();
		break;

	case T_TITLE:
		scan__();
		scan__test(T_LPAR, dir_stop, "Expecting " "(");
		scan__();
		scan__test(T_DQUOTE, prod_stop, "Expecting code item");
		dir_title = scan__sym->id + 1;
		scan__();
		scan__test(T_RPAR, dir_stop, "Expecting " ")");
		scan__();
		break;

	case T_SUFFIX:
		scan__();
		scan__test(T_LPAR, dir_stop, "Expecting " "(");
		scan__();
		scan__test(T_DQUOTE, prod_stop, "Expecting code item");
		dir_suffix = scan__sym->id + 1;
		scan__();
		scan__test(T_RPAR, dir_stop, "Expecting " ")");
		scan__();
		break;

	case T_OUTPUT_FILE:
		scan__();
		scan__test(T_LPAR, dir_stop, "Expecting " "(");
		scan__();
		scan__test(T_DQUOTE, prod_stop, "Expecting code item");
		dir_output_file = scan__sym->id + 1;
		scan__();
		scan__test(T_RPAR, dir_stop, "Expecting " ")");
		scan__();
		break;

	case T_SET_SIZE:
		scan__();
		scan__test(T_LPAR, dir_stop, "Expecting " "(");
		scan__();
		scan__test(P_NUMBER, dir_stop, "Expecting " "NUMBER");
		dir_set_size = scan__sym->number;
		scan__();
		scan__test(T_RPAR, dir_stop, "Expecting " ")");
		scan__();
		break;

	case T_HASH_SIZE:
		scan__();
		scan__test(T_LPAR, dir_stop, "Expecting " "(");
		scan__();
		scan__test(P_NUMBER, dir_stop, "Expecting " "NUMBER");
		dir_hash_size = scan__sym->number;
		scan__();
		scan__test(T_RPAR, dir_stop, "Expecting " ")");
		scan__();
		break;

	case T_HASH_PRIME:
		scan__();
		scan__test(T_LPAR, dir_stop, "Expecting " "(");
		scan__();
		scan__test(P_NUMBER, dir_stop, "Expecting " "NUMBER");
		dir_hash_prime = scan__sym->number;
		scan__();
		scan__test(T_RPAR, dir_stop, "Expecting " ")");
		scan__();
		break;

	case T_TEXT_SIZE:
		scan__();
		scan__test(T_LPAR, dir_stop, "Expecting " "(");
		scan__();
		scan__test(P_NUMBER, dir_stop, "Expecting " "NUMBER");
		dir_text_size = scan__sym->number;
		scan__();
		scan__test(T_RPAR, dir_stop, "Expecting " ")");
		scan__();
		break;

	case T_TAB_WIDTH:
		scan__();
		scan__test(T_LPAR, dir_stop, "Expecting " "(");
		scan__();
		scan__test(P_NUMBER, dir_stop, "Expecting " "NUMBER");
		dir_tab_width = scan__sym->number;
		scan__();
		scan__test(T_RPAR, dir_stop, "Expecting " ")");
		scan__();
		break;

	case T_MAX_ERRORS:
		scan__();
		scan__test(T_LPAR, dir_stop, "Expecting " "(");
		scan__();
		scan__test(P_NUMBER, dir_stop, "Expecting " "NUMBER");
		dir_max_errors = scan__sym->number;
		scan__();
		scan__test(T_RPAR, dir_stop, "Expecting " ")");
		scan__();
		break;

	case T_MAX_WARNINGS:
		scan__();
		scan__test(T_LPAR, dir_stop, "Expecting " "(");
		scan__();
		scan__test(P_NUMBER, dir_stop, "Expecting " "NUMBER");
		dir_max_warnings = scan__sym->number;
		scan__();
		scan__test(T_RPAR, dir_stop, "Expecting " ")");
		scan__();
		break;

	case T_TEXT_MODE:
		scan__();
		dir_text_mode = 1;
		break;

	case T_CASE_INSENSITIVE:
		scan__();
		dir_case_insensitive = 1;
		break;

	case T_SHOW_SKIPS:
		scan__();
		dir_show_skips = 1;
		break;

	case T_NEWLINE_VISIBLE:
		scan__();
		dir_newline_visible = 1;
		break;

	case T_COMMENTS_VISIBLE:
		scan__();
		dir_comments_visible = 1;
		break;
	}
}

void unit(void)									/* parse a file */
{
	scan__test_set(unit_first, prod_stop, "Expecting product or directive");

	while (set__inc_element(unit_first, scan__sym->token))
	{
		if (set__inc_element(prod_first, scan__sym->token))
			prod();
		else if (set__inc_element(dir_first, scan__sym->token))
			dir();

	}

	scan__test(P_EOF, prod_stop, "Expecting end of file");
}

int main(int argc, char *argv[])
{
	unsigned tabwidth = 2,				/* tab expansion width */
	 textsize = 20000,						/* size of scanner text array */
	 symbol__statistics = 0;				/* show symbol table statistics flag */
	symbol__ *base;									/* pointer to production chain scope */

	while (--argc > 0)
	{
		if ((*++argv)[0] == '-')		/* switch */
		{
			switch ((*argv)[1])
			{
			case 'b':
				bnffilename = *argv + 2;
				break;
			case 'o':
				outputfilename = *argv + 2;
				break;
			case 's':
				scan__symbol_echo(1);
				break;
			case 'S':
				symbol__statistics = 1;
				break;
			case 'l':
				scan__line_echo(1);
				break;
			case 't':
				{
					sscanf(*argv + 2, "%u", &tabwidth);
					scan__line_echo(tabwidth);
				}
				break;
			case 'T':
				{
					sscanf(*argv + 2, "%u", &textsize);
				}
				break;
			case 'v':
				verbose = 1;
				break;
			case 'F':
				force = 1;
				break;
			default:
				{
					printf("\nUnrecognised option -%c\n", (*argv)[1]);
					help("");
				}
			}
		}
		else
			sourcefilename = *argv;
	}

	if (sourcefilename == NULL)
		help("No source file specified");

	set__init(RDP_SET_SIZE);
	load_sets();

	symbol__init(101, 31);
	load_keywords();
	base = symbol__new_scope("parser");	/* create bnf scope */
	scan__show_skips(1);
	scan__summarise_errors(verbose);
	scan__init(textsize, 25, 100);

#define FILE_MODE
#ifdef FILE_MODE
	if (scan__open_file(scan__default_filetype(sourcefilename, "bnf")) == NULL)
		help("Can't open source file");
#else
	if (scan__open_text("Text mode",
										 scan__load_file(scan__default_filetype(sourcefilename, "bnf"))
										 ) == NULL)
												help("Can't open source file");
#endif

	if (verbose)
		printf("\nRecursive descent parser generator V1.00 (c) Adrian Johnstone 1993\n\n");

  pre_process();

	unit();												/* call parser start production */

	if (symbol__statistics)
		symbol__print_statistics();

	return post_process(outputfilename, force, base);
}

/* End of rdp.c */
