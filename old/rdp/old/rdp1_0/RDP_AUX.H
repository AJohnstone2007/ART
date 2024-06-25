/****************************************************************************
*
* RDP release 1.00 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 14 Feb 1994
*
* rdp_aux.h - rdp semantic routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#ifndef RDP_SUPP_H
#define RDP_SUPP_H

#define RDP_SET_SIZE 200
#define line_break 60

extern char
*bnffilename,										/* expanded BNF file name */
*start_id,											/* identifier for first production: used as start symbol */
 verbose,												/* verbose mode flag */
*primary_id;										/* identifier for parent production */

extern unsigned component;			/* sub-production component number */

extern char
*dir_title,											/* string from TITLE directive */
*dir_suffix,										/* string from SUFFIX directive */
*dir_pre_process,								/* string from PRE_PROCESS directive */
*dir_post_process,							/* string from POST_PROCESS directive */
*dir_output_file;								/* string from OUTPUT_FILE directive */

extern unsigned dir_set_size,		/* number from SET_SIZE directive */
 dir_text_mode,									/* TEXT_MODE flag */
 dir_case_insensitive,					/* CASE_INSENSITIVE flag */
 dir_show_skips,								/* SHOW_SKIPS flag */
 dir_newline_visible,						/* NEWLINE_VISIBLE flag */
 dir_comments_visible,					/* COMMENTS_VISIBLE flag */
 dir_hash_size,									/* HASH_SIZE directive */
 dir_hash_prime,								/* HASH_PRIME dirctive */
 dir_tab_width,									/* TAB_WIDTH directive */
 dir_text_size,									/* TEXT_SIZE directive */
 dir_max_errors,								/* MAX_ERRORS directive */
 dir_max_warnings;							/* MAX_WARNINGS directive */

struct string_list
{
	char *str1,
	*str2;
	struct string_list *next;
};

extern struct string_list
*dir_help,											/* strings from HELP directives */
*dir_include;										/* strings from INCLUDE directives */

typedef struct list_node
{
	char *return_name;
	symbol__ *production;
	struct list_node *next;
} list;

struct data_node
{
	enum													/* Production classifications */
	{
		K_NUMBER_PRIMITIVE, K_STRING_PRIMITIVE, K_CODE, K_TOKEN,
		K_PRIMARY, K_SEQUENCE, K_DO_FIRST, K_LIST, K_CONDITIONAL,
		K_ITERATION
	} kind;
 int contains_null:
	 1,														/* for quick first calculation */
  been_defined:
	 1,														/* hass appeared on LHS of ::= */
  in_use:
	 1,														/* production being checked flag */
  first_done:
	 1,														/* first() completed on this production */
	follow_done:
	 1;														/* follow() completed on this production */
	set__ first;										/* set of first symbols in this production */
	unsigned first_cardinality;		/* number of elements in first set */
	set__ follow;										/* set of follow symbols in this production */
	unsigned follow_cardinality;	/* number of elements in follow set */
	unsigned token_value;					/* token value for tokens */
	struct list_node *list;				/* list of alternatives or items */
	symbol__ *supplementary_token;	/* spare token pointer */
	unsigned count;								/* list length */
	char *return_type;						/* return_type name */
	unsigned return_type_stars;		/* number of levels of indirection in return type */
	int extended_class;						/* extended keyword class number */
	char *close;									/* extended keyword close string */
};

/*macro for retrieving fields */
#define DATA(n,field)	(((struct data_node*) n->data)->field)

/* globally visible functions */

void check_prod_name_valid(char *id);
void check_token_valid(char *id);
symbol__ *find(char *id, unsigned kind, int must_be_new);
int post_process(char *outputfilename, int force, symbol__ * base);
void pre_process(void);

#endif

/* End of rdp_aux.h */
