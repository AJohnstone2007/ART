/****************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 8 April 1996
*
* rdp_gram.c - rdp grammar checking routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#include <string.h>
#include <ctype.h>
#include <time.h>
#include "memalloc.h"
#include "scan.h"
#include "set.h"
#include "symbol.h"
#include "textio.h"
#include "rdp_aux.h"
#include "rdp_gram.h"
#include "rdp_prnt.h"
#include "rdp.h"

static int rdp_follow_changed; /* repeat until done flag for follow sets */

void rdp_check_eoln(char *id)
{
  if (strcmp(id, "EOLN") == 0)
    rdp_dir_newline_visible = 1; /* Grammar contains an explicit EOLN */
}

void rdp_check_token_valid(char *id)
{
  if (id == NULL)
    return;

  if (*id == 0)
    text_message(TEXT_ERROR_ECHO, "Empty tokens are not allowed: use [ ... ] instead\n");
  /* Test for embedded spaces in token */
  {
    int bad = 0;

    while (*id != 0)
    {
      bad |= !isgraph(*id);
      id++;
    }

    if (bad)
      text_message(TEXT_ERROR_ECHO, "Tokens must not contain spaces or control characters\n");
  }
}

void rdp_check_string_token_valid(char *id)
{
  rdp_check_token_valid(id);   /* make sure it's not empty or contains spaces */
  if (*(id + 2) != 0)
    text_message(TEXT_ERROR_ECHO,"String delimiter tokens must be exactly one character long\n");
}

void rdp_check_comment_token_valid(char *id)
{
  rdp_check_token_valid(id);   /* make sure it's not empty or contains spaces */
  if (!(*(id + 2) == 0 || *(id + 3) == 0))
    text_message(TEXT_ERROR_ECHO, "Comment delimiter tokens must be less than three characters long\n");
}

void rdp_check_prod_name_valid(char *id)
{
  if ((strncmp("rdp_", id, 4) == 0) ||
      (strncmp("args_", id, 5) == 0) ||
      (strncmp("mem_", id, 4) == 0) ||
      (strncmp("set_", id, 4) == 0) ||
      (strncmp("scan_", id, 5) == 0) ||
      (strncmp("sym_", id, 4) == 0) ||
      (strncmp("text_", id, 5) == 0))
    text_message(TEXT_ERROR_ECHO, "Identifier '%s' begins with a reserved name\n", id);
}

int rdp_is_valid_C_id(char *s)
{
  int temp;

  temp = (isalpha(*s) || *s == '_');
  while (*++s != '\0')
    temp = temp && (isalnum(*s) || *s == '_');
  return (temp);
}

static void rdp_count_productions(void * base)
{
  unsigned primaries = 0,
   internals = 0,
   codes = 0;

  rdp_data *temp = (rdp_data *) symbol_next_symbol_in_scope(base);

  while (temp != NULL)
  {
    if (temp->kind == K_PRIMARY)
      primaries++;
    else if (temp->kind == K_CODE)
      codes++;
    else
      internals++;

    temp = (rdp_data*) symbol_next_symbol_in_scope(temp);
  }

  if (rdp_verbose)
    text_message(TEXT_INFO, "%u productions, %u tokens, %u actions, %u subproductions\n",
	    primaries, rdp_token_count - SCAN_P_TOP + 1, codes, internals);
}

static void rdp_first(rdp_data * prod)
{
  if (prod->in_use)       /* something has gone wrong */
  {
    text_message(TEXT_ERROR, "LL(1) violation - production '%s' is left recursive\n", prod->id); /* and return */
    prod->ll1_violation = 1;
    return;
  }

  if (!prod->first_done)  /* something to do */
  {
    rdp_list *list = prod->list; /* set up alternates pointer */

    prod->in_use = 1;     /* mark this production as being processed */

    if (prod->kind == K_SEQUENCE) /* sequences are treated differently */
    {
      prod->contains_null = 1;  /* set up list flag */
      while (list != NULL && prod->contains_null) /* scan until non-empty alternate is found */
      {
	if (!list->production->first_done)  /* do first */
	  rdp_first(list->production);

	set_unite_set(&prod->first, &list->production->first); /* add alternate first set to production first set */
	prod->contains_null = list->production->contains_null;  /* set contains_null flag */

	list = list->next;
      }
    }
    else
      while (list != NULL)      /* scan all alternates */
      {
	if (!list->production->first_done)  /* do first */
	  rdp_first(list->production);

	set_unite_set(&prod->first, &list->production->first); /* add alternate first set to production first set */
	prod->contains_null |= list->production->contains_null; /* OR in contains_null flag */
	list = list->next;
      }
    prod->in_use = 0;     /* production is no longer in use */
    prod->first_done = 1; /* first set is now complete */
    /* and set cardinality */
    prod->first_cardinality = set_cardinality(&prod->first);
  }
}

/* scan a sequence production for follow set changes */
static void rdp_follow_sequence(rdp_data * prod)
{
  rdp_list *check = prod->list;  /* pointer to sequence list */

  while (check != NULL)         /* scan entire sequence and add to follow sets */
  {
    rdp_list *following = check; /* temporary to look at followers */
    unsigned old_cardinality = check->production->follow_cardinality;

    do                          /* scan up list adding first sets of trailing productions */
    {
      following = following->next;
      if (following == NULL)    /* We hit end of sequence */
	set_unite_set(&check->production->follow, &prod->follow);
      else
	set_unite_set(&check->production->follow, &following->production->first);
    }
    while (following != NULL && following->production->contains_null);

    /* Update cardinality changed flag */
    rdp_follow_changed |= ((check->production->follow_cardinality =
			     set_cardinality(&check->production->follow)
                             ) != old_cardinality);

    check = check->next;        /* step to next item in sequence */
  }
}

/* scan an alternate for follow set changes */
static void rdp_follow_alternate(rdp_data * prod)
{
  rdp_list *check = prod->list;  /* pointer to alternate list */

  while (check != NULL)
  {
    unsigned old_cardinality = check->production->follow_cardinality;

    set_unite_set(&check->production->follow, &prod->follow);

    rdp_follow_changed |= ((check->production->follow_cardinality =
			     set_cardinality(&check->production->follow)
			     ) != old_cardinality);
    check = check->next;
  }
}

static void rdp_find_follow(void * base)
{
  rdp_data *temp;
  unsigned follow_pass = 0;

  do
  {
    follow_pass++;
    rdp_follow_changed = 0;
    temp = (rdp_data*) symbol_next_symbol_in_scope(base);
    while (temp != NULL)
    {
      if (temp->kind == K_SEQUENCE) /* only need to scan sequences */
	rdp_follow_sequence(temp);
      else
	rdp_follow_alternate(temp);
      temp = (rdp_data*) symbol_next_symbol_in_scope(temp);
    }
  }
  while (rdp_follow_changed);

  if (rdp_verbose)
    text_message(TEXT_INFO, "Follow sets stabilised after %u pass%s\n", follow_pass, follow_pass == 1 ? "" : "es");

}

static int rdp_check_identifier(char *id)
{
  rdp_data* s = (rdp_data*) symbol_lookup_key(rdp, &id, NULL);
  if (s!=NULL)
  {
    if (s->kind == K_PRIMARY)
    {
      text_message(TEXT_ERROR, "Identifier '%s' is a C++ reserved word or library identifier\n", id);
      return 1;
    }
  }
  return 0;
}

static int rdp_check_reserved_words(void)
{
  int bad = 0, temp = 0;
  char *rdp_reserved_words[] = {RDP_RESERVED_WORDS, NULL};

  while (rdp_reserved_words[temp] != NULL)
    bad |= rdp_check_identifier(rdp_reserved_words[temp++]);

  return bad;
}

/* check for empty alternates. mark up code sequences while we're at it */
static int rdp_check_empties(void * base)
{
  int bad = 0;
  rdp_data * temp = (rdp_data*) symbol_next_symbol_in_scope(base);

  while (temp != NULL)
  {
    rdp_list *list = temp->list;
    int k = temp->kind,
     bad_alternate = 1;

    if (k == K_PRIMARY && temp->call_count == 0 )
      text_message(TEXT_WARNING, "Production '%s' never called so deleted\n", temp->id);

    if (list == NULL && k == K_PRIMARY)
    {
      text_message(TEXT_ERROR, "Production '%s' is empty\n", temp->id);
      bad = 1;
    }

    if (k == K_SEQUENCE)        /* check for empty alternates and mark up code */
    {
      while (list != NULL)
      {
	if (list->production->kind == K_CODE) /* check code position */
	  if (list->next == NULL)                   /* last in list? */
	    list->production->code_terminator = 1;
	  else
	    if (list->next->production->kind == K_CODE) /* is the next one code? */
	      list->next->production->code_successor = 1; /* next one is code successor */
	    else
	      list->production->code_terminator =1;  /* this one is code terminator */

	if (list->production->kind != K_CODE)
	  bad_alternate = 0;
	list = list->next;
      }
    }
    else
      bad_alternate = 0;

    if (bad_alternate)
    {
      if (temp->list == NULL)
      {
	text_message(TEXT_ERROR, "LL(1) violation - subproduction '%s' is empty\n", temp->id);
	temp->ll1_violation = 1;
      }
      else
      {
	temp->code_only = 1;
      }
      bad = 1;
    }
    temp = (rdp_data*) symbol_next_symbol_in_scope(temp);
  }

  /* Now go over again updating primaries to mark code only productions */
  temp = (rdp_data*) symbol_next_symbol_in_scope(base);

  while (temp != NULL)
  {
    if (temp->kind == K_PRIMARY && temp->list != NULL)
      if (temp->list->next == NULL && temp->list->production->code_only)
	temp->code_only = 1;
    temp = (rdp_data*) symbol_next_symbol_in_scope(temp);
  }
  return bad;
}

static void rdp_find_first(void * base)
{
  rdp_data* temp = (rdp_data*) symbol_next_symbol_in_scope(base);

  while (temp != NULL)
  {
    rdp_first(temp);
    temp = (rdp_data*) symbol_next_symbol_in_scope(temp);
  }
}

static int rdp_check_disjoint(void * base)
{
  int bad = 0;
  set_ work = SET_NULL;

  rdp_data * temp = (rdp_data*) symbol_next_symbol_in_scope(base);

  while (temp != NULL)
  {
    if (set_includes_element(&rdp_production_set, temp->kind) && temp->kind != K_SEQUENCE)
    {
      rdp_list *left = temp->list;

      while (left != NULL)
      {
	rdp_list *right = left->next;

	while (right != NULL)
	{
	  set_assign_set(&work, &left->production->first);
	  set_intersect_set(&work, &right->production->first);

	  if (set_cardinality(&work) != 0)
	  {
	    char *tempstr = strstr(temp->id, "_"); /* find _ in name */
	    ptrdiff_t length;

	    if (tempstr != NULL)
	      length = (ptrdiff_t) (tempstr - temp->id); /* length of prefix */
	    else
	      length = strlen(temp->id);

	    text_message(TEXT_ERROR, "LL(1) violation - production '%.*s'\n", (int) length, temp->id);
	    text_printf(" alternates %s ::= ", left->production->id);
	    rdp_print_sub_item(left->production, 1);
	    text_printf(".\n and %s ::= ", right->production->id);
	    rdp_print_sub_item(right->production, 1);
	    text_printf(".\n share these start tokens: ");
	    set_print_set(&work, rdp_token_string, 78);
	    text_printf("\n");
	    left->production->ll1_violation = 1;
	    right->production->ll1_violation = 1;
	    bad = 1;
          }
	  right = right->next;
	}
	left = left->next;
      }
    }
    temp = (rdp_data*) symbol_next_symbol_in_scope(temp);
  }
  return bad;
}

static int rdp_check_nullable(void * base)
{
  int bad = 0;
  rdp_data* temp = (rdp_data*) symbol_next_symbol_in_scope(base);
  set_ work = SET_NULL;

  while (temp != NULL)
  {
    if (temp->contains_null && (temp->kind != K_CODE) )
    {
      set_assign_set(&work, &temp->first);
      set_intersect_set(&work, &temp->follow);

      if (set_cardinality(&work) != 0)
      {
	char *tempstr = strstr(temp->id, "_"); /* find _ in name */
	ptrdiff_t length;

	if (tempstr != NULL)
	  length = (ptrdiff_t) (tempstr - temp->id); /* length of prefix */
	else
	  length = strlen(temp->id);

	text_message(TEXT_ERROR, "LL(1) violation - production '%.*s' subproduction\n %s ::= ", (int) length, temp->id, temp->id);
	rdp_print_sub_item(temp, 1);
	text_printf(".\n contains null but first and follow sets both include: ");

	set_print_set(&work, rdp_token_string, 78);
	text_printf("\n");
	temp->ll1_violation = 1;
	bad = 1;
      }
    }
    temp = (rdp_data*) symbol_next_symbol_in_scope(temp);
  }
  return bad;
}

static void rdp_update_follow_sets(void * base)
{
  rdp_data* temp = (rdp_data*) symbol_next_symbol_in_scope(base);

  while (temp != NULL)
  {
    if (temp->kind == K_LIST && temp->hi != 1 && temp->supplementary_token == NULL)
    {
      set_unite_set(&temp->follow, &temp->first);
      temp->follow_cardinality = set_cardinality(&temp->follow);
    }
    temp = (rdp_data*) symbol_next_symbol_in_scope(temp);
  }
}

int rdp_bad_grammar(void * base)
{
  int bad = 0;

  /* Count productions and produce statistics */
  rdp_count_productions(base);

  /* find first sets */
  if (rdp_verbose)
    text_message(TEXT_INFO,"Generating first sets\n");
  rdp_find_first(base);

  /* find follow sets */
  if (rdp_verbose)
    text_message(TEXT_INFO, "Generating follow sets\n");
  rdp_find_follow(base);

  /* check for C reserved words */
  if (rdp_verbose)
    text_message(TEXT_INFO, "Checking for clashes with reserved words\n");
  bad |= rdp_check_reserved_words();

  /* Check for empties */
  if (rdp_verbose)
    text_message(TEXT_INFO, "Checking for empty alternates\n");
  bad |= rdp_check_empties(base);

  /* check that for each production, all alternates have unique start tokens */
  if (rdp_verbose)
    text_message(TEXT_INFO, "Checking for disjoint first sets\n");
  bad |= rdp_check_disjoint(base);

  /* check that first(a) - follow (a) is empty for nullable a */
  if (rdp_verbose)
    text_message(TEXT_INFO, "Checking nullable productions\n");
  bad |= rdp_check_nullable(base);

  /* add first() to follow() for iterations so that error handling doesn't just eat entire file! */
  if (rdp_verbose)
    text_message(TEXT_INFO, "Updating follow sets\n");
  rdp_update_follow_sets(base);
  /* re-close follow sets */
  rdp_find_follow(base);

  return bad;
}

/* End of rdp_gram.c */
