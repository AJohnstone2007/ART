/****************************************************************************
*
* RDP release 1.40 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 30 Jan 1995
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

static int rdp__follow_changed; /* repeat until done flag for follow sets */

void rdp__check_eoln(char *id)
{
  if (strcmp(id, "EOLN") == 0)
    rdp__dir_newline_visible = 1; /* Grammar contains an explicit EOLN */
}

void rdp__check_token_valid(char *id)
{
  if (id == NULL)
    return;

  if (*id == 0)
    text__message(TEXT__ERROR_ECHO, "empty tokens are not allowed: use [ ... ] instead\n");
  /* Test for embedded spaces in token */
  {
    int bad = 0;

    while (*id != 0)
    {
      bad |= !isgraph(*id);
      id++;
    }

    if (bad)
      text__message(TEXT__ERROR_ECHO, "tokens must not contain spaces or control characters\n");
  }
}

void rdp__check_string_token_valid(char *id)
{
  rdp__check_token_valid(id);   /* make sure it's not empty or contains spaces */
  if (*(id + 2) != 0)
    text__message(TEXT__ERROR_ECHO,"string delimiter tokens must be exactly one character long\n");
}

void rdp__check_comment_token_valid(char *id)
{
  rdp__check_token_valid(id);   /* make sure it's not empty or contains spaces */
  if (!(*(id + 2) == 0 || *(id + 3) == 0))
    text__message(TEXT__ERROR_ECHO, "comment delimiter tokens must be less than three characters long\n");
}

void rdp__check_prod_name_valid(char *id)
{
  /* Test for two consecutive _ in token */
  char *c = id;
  int bad = 0;

  while (*c != 0)
    bad |= (*c++ == '_' && *c == '_');
  if (bad)
    text__message(TEXT__ERROR_ECHO, "identifier '%s' contains two consecutive underscores\n", id);
}

int rdp__is_valid_C_id(char *s)
{
  int temp;

  temp = (isalpha(*s) || *s == '_');
  while (*++s != '\0')
    temp = temp && (isalnum(*s) || *s == '_');
  return (temp);
}

static void rdp__count_productions(void * base)
{
  unsigned primaries = 0,
   internals = 0,
   codes = 0;

  rdp__data *temp = (rdp__data *) symbol__next_symbol_in_scope(base);

  while (temp != NULL)
  {
    if (temp->kind == K__PRIMARY)
      primaries++;
    else if (temp->kind == K__CODE)
      codes++;
    else
      internals++;

    temp = (rdp__data*) symbol__next_symbol_in_scope(temp);
  }

  if (rdp__verbose)
    text__message(TEXT__INFO, "%u productions, %u tokens, %u actions and %u subproductions\n",
	    primaries, rdp__token_count - T__TOP + 1, codes, internals);
}

static void rdp__first(rdp__data * prod)
{
  if (prod->in_use)       /* something has gone wrong */
  {
    text__message(TEXT__ERROR, "LL(1) violation - production '%s' is left recursive\n", prod->id); /* and return */
    return;
  }

  if (!prod->first_done)  /* something to do */
  {
    rdp__list *list = prod->list; /* set up alternates pointer */

    prod->in_use = 1;     /* mark this production as being processed */

    if (prod->kind == K__SEQUENCE) /* sequences are treated differently */
    {
      prod->contains_null = 1;  /* set up list flag */
      while (list != NULL && prod->contains_null) /* scan until non-empty alternate is found */
      {
	if (!list->production->first_done)  /* do first */
	  rdp__first(list->production);

	set__unite_set(&prod->first, &list->production->first); /* add alternate first set to production first set */
	prod->contains_null = list->production->contains_null;  /* set contains_null flag */

	list = list->next;
      }
    }
    else
      while (list != NULL)      /* scan all alternates */
      {
	if (!list->production->first_done)  /* do first */
	  rdp__first(list->production);

	set__unite_set(&prod->first, &list->production->first); /* add alternate first set to production first set */
	prod->contains_null |= list->production->contains_null; /* OR in contains_null flag */
	list = list->next;
      }
    prod->in_use = 0;     /* production is no longer in use */
    prod->first_done = 1; /* first set is now complete */
    /* and set cardinality */
    prod->first_cardinality = set__cardinality(&prod->first);
  }
}

/* scan a sequence production for follow set changes */
static void rdp__follow_sequence(rdp__data * prod)
{
  rdp__list *check = prod->list;  /* pointer to sequence list */

  while (check != NULL)         /* scan entire sequence and add to follow sets */
  {
    rdp__list *following = check; /* temporary to look at followers */
    unsigned old_cardinality = check->production->follow_cardinality;

    do                          /* scan up list adding first sets of trailing productions */
    {
      following = following->next;
      if (following == NULL)    /* We hit end of sequence */
	set__unite_set(&check->production->follow, &prod->follow);
      else
	set__unite_set(&check->production->follow, &following->production->first);
    }
    while (following != NULL && following->production->contains_null);

    /* Update cardinality changed flag */
    rdp__follow_changed |= ((check->production->follow_cardinality =
			     set__cardinality(&check->production->follow)
                             ) != old_cardinality);

    check = check->next;        /* step to next item in sequence */
  }
}

/* scan an alternate for follow set changes */
static void rdp__follow_alternate(rdp__data * prod)
{
  rdp__list *check = prod->list;  /* pointer to alternate list */

  while (check != NULL)
  {
    unsigned old_cardinality = check->production->follow_cardinality;

    set__unite_set(&check->production->follow, &prod->follow);

    rdp__follow_changed |= ((check->production->follow_cardinality =
			     set__cardinality(&check->production->follow)
			     ) != old_cardinality);
    check = check->next;
  }
}

static void rdp__find_follow(void * base)
{
  rdp__data *temp;
  unsigned follow_pass = 0;

  do
  {
    follow_pass++;
    rdp__follow_changed = 0;
    temp = (rdp__data*) symbol__next_symbol_in_scope(base);
    while (temp != NULL)
    {
      if (temp->kind == K__SEQUENCE) /* only need to scan sequences */
	rdp__follow_sequence(temp);
      else
	rdp__follow_alternate(temp);
      temp = (rdp__data*) symbol__next_symbol_in_scope(temp);
    }
  }
  while (rdp__follow_changed);

  if (rdp__verbose)
    text__message(TEXT__INFO, "follow sets stabilised after %u pass%s\n", follow_pass, follow_pass == 1 ? "" : "es");

}

static int rdp__check_identifier(char *id)
{
  rdp__data* s = (rdp__data*) symbol__lookup_key(rdp, id);
  if (s!=NULL)
  {
    if (s->kind == K__PRIMARY)
    {
      text__message(TEXT__ERROR, "identifier '%s' is a C++ reserved word or library identifier\n", id);
      return 1;
    }
  }
  return 0;
}

static int rdp__check_reserved_words(void)
{
  int bad = 0, temp = 0;
  char *rdp__reserved_words[] = {RDP__RESERVED_WORDS, NULL};

  while (rdp__reserved_words[temp] != NULL)
    bad |= rdp__check_identifier(rdp__reserved_words[temp++]);

  return bad;
}

/* check for empty alternates. mark up code sequences while we're at it */
static int rdp__check_empties(void * base)
{
  int bad = 0;
  rdp__data * temp = (rdp__data*) symbol__next_symbol_in_scope(base);

  while (temp != NULL)
  {
    rdp__list *list = temp->list;
    int k = temp->kind,
     bad_alternate = 1;

    if (k == K__PRIMARY && temp->call_count == 0 )
      text__message(TEXT__WARNING, "production '%s' will not appear in the output file\n", temp->id);

    if (list == NULL && k == K__PRIMARY)
    {
      text__message(TEXT__ERROR, "production '%s' is empty\n", temp->id);
      bad = 1;
    }

    if (k == K__SEQUENCE)        /* check for empty alternates and mark up code */
    {
      while (list != NULL)
      {
	if (list->production->kind == K__CODE) /* check code position */
	  if (list->next == NULL)                   /* last in list? */
	    list->production->code_terminator = 1;
	  else
	    if (list->next->production->kind == K__CODE) /* is the next one code? */
	      list->next->production->code_successor = 1; /* next one is code successor */
	    else
	      list->production->code_terminator =1;  /* this one is code terminator */

	if (list->production->kind != K__CODE)
	  bad_alternate = 0;
	list = list->next;
      }
    }
    else
      bad_alternate = 0;

    if (bad_alternate)
    {
      if (temp->list == NULL)
	text__message(TEXT__ERROR, "LL(1) violation - subproduction '%s' is empty\n", temp->id);
      else
      {
	temp->code_only = 1;
      }
      bad = 1;
    }
    temp = (rdp__data*) symbol__next_symbol_in_scope(temp);
  }

  /* Now go over again updating primaries to mark code only productions */
  temp = (rdp__data*) symbol__next_symbol_in_scope(base);

  while (temp != NULL)
  {
    if (temp->kind == K__PRIMARY && temp->list != NULL)
      if (temp->list->next == NULL && temp->list->production->code_only)
	temp->code_only = 1;
    temp = (rdp__data*) symbol__next_symbol_in_scope(temp);
  }
  return bad;
}

static void rdp__find_first(void * base)
{
  rdp__data* temp = (rdp__data*) symbol__next_symbol_in_scope(base);

  while (temp != NULL)
  {
    rdp__first(temp);
    temp = (rdp__data*) symbol__next_symbol_in_scope(temp);
  }
}

static int rdp__check_disjoint(void * base)
{
  int bad = 0;
  set__ work = SET__NULL;

  rdp__data * temp = (rdp__data*) symbol__next_symbol_in_scope(base);

  while (temp != NULL)
  {
    if (set__includes_element(&rdp__production_set, temp->kind) && temp->kind != K__SEQUENCE)
    {
      rdp__list *left = temp->list;

      while (left != NULL)
      {
	rdp__list *right = left->next;

	while (right != NULL)
	{
	  set__assign_set(&work, &left->production->first);
	  set__intersect_set(&work, &right->production->first);

	  if (set__cardinality(&work) != 0)
	  {
	    char *tempstr = strstr(temp->id, "__"); /* find __ in name */
	    size_t length;

	    if (tempstr != NULL)
	      length = (size_t) (tempstr - temp->id); /* length of prefix */
	    else
	      length = strlen(temp->id);

	    text__message(TEXT__ERROR, "LL(1) violation - production '%.*s'\n", length, temp->id);
	    text__printf(" alternates %s ::= ", left->production->id);
	    rdp__print_sub_item(left->production, 1);
	    text__printf(".\n and %s ::= ", right->production->id);
	    rdp__print_sub_item(right->production, 1);
	    text__printf(".\n share these start tokens: ");
	    set__print_set(&work, rdp__token_string);
	    text__printf("\n");
	    bad = 1;
          }
	  right = right->next;
	}
	left = left->next;
      }
    }
    temp = (rdp__data*) symbol__next_symbol_in_scope(temp);
  }
  return bad;
}

static int rdp__check_nullable(void * base)
{
  int bad = 0;
  rdp__data* temp = (rdp__data*) symbol__next_symbol_in_scope(base);
  set__ work = SET__NULL;

  while (temp != NULL)
  {
    if (temp->contains_null && (temp->kind != K__CODE) )
    {
      set__assign_set(&work, &temp->first);
      set__intersect_set(&work, &temp->follow);

      if (set__cardinality(&work) != 0)
      {
	char *tempstr = strstr(temp->id, "__"); /* find __ in name */
	size_t length;

	if (tempstr != NULL)
	  length = (size_t) (tempstr - temp->id); /* length of prefix */
	else
	  length = strlen(temp->id);

	text__message(TEXT__ERROR, "LL(1) violation - production '%.*s' subproduction\n %s ::= ", length, temp->id, temp->id);
	rdp__print_sub_item(temp, 1);
	text__printf(".\n contains null but first and follow sets both include: ");

	set__print_set(&work, rdp__token_string);
	text__printf("\n");
	bad = 1;
      }
    }
    temp = (rdp__data*) symbol__next_symbol_in_scope(temp);
  }
  return bad;
}

static void rdp__update_follow_sets(void * base)
{
  rdp__data* temp = (rdp__data*) symbol__next_symbol_in_scope(base);

  while (temp != NULL)
  {
    if (temp->kind == K__ITERATION)
    {
      set__unite_set(&temp->follow, &temp->first);
      temp->follow_cardinality = set__cardinality(&temp->follow);
    }
    temp = (rdp__data*) symbol__next_symbol_in_scope(temp);
  }
}

int rdp__bad_grammar(void * base)
{
  int bad = 0;

  /* Count productions and produce statistics */
  rdp__count_productions(base);

  /* find first sets */
  if (rdp__verbose)
    text__message(TEXT__INFO,"generating first sets\n");
  rdp__find_first(base);

  /* find follow sets */
  if (rdp__verbose)
    text__message(TEXT__INFO, "generating follow sets\n");
  rdp__find_follow(base);

  /* check for C reserved words */
  if (rdp__verbose)
    text__message(TEXT__INFO, "checking for clashes with reserved words\n");
  bad |= rdp__check_reserved_words();

  /* Check for empties */
  if (rdp__verbose)
    text__message(TEXT__INFO, "checking for empty alternates\n");
  bad |= rdp__check_empties(base);

  /* check that for each production, all alternates have unique start tokens */
  if (rdp__verbose)
    text__message(TEXT__INFO, "checking for disjoint first sets\n");
  bad |= rdp__check_disjoint(base);

  /* check that first(a) - follow (a) is empty for nullable a */
  if (rdp__verbose)
    text__message(TEXT__INFO, "checking nullable productions\n");
  bad |= rdp__check_nullable(base);

  /* add first() to follow() for iterations so that error handling doesn't just eat entire file! */
  if (rdp__verbose)
    text__message(TEXT__INFO, "updating follow sets\n");
  rdp__update_follow_sets(base);
  /* re-close follow sets */
  rdp__find_follow(base);

  return bad;
}

/* End of rdp_gramm.c */
