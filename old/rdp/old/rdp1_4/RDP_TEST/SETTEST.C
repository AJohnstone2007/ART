/****************************************************************************
*
* RDP release 1.40 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 14 Jan 1995
*
* settest.c - test all set.c routines. See settest.ok for correct output
*
* This file may be freely distributed. Please mail improvements to the author.
*
****************************************************************************/
#include "stdio.h"
#include "set.h"

char* elements = "zero\0one\0two\0three\0four\0five\0six\0seven\0eight\0nine\0"
		 "ten\0eleven\0twelve\0thirteen\0fourteen\0fifteen\0sixteen\0";

static void print_set(char *mess, set__ *s, char *elements)
{
  printf("%s {", mess);
  set__print_set(s, NULL);
  printf("} = {");

  set__print_set(s, elements );
  printf("}, cardinality %u, size %u\n", set__cardinality(s), s->length);
}

int main(void)
{
  set__ null = SET__NULL, first, second;

  first = second = null;

  printf("\n\nSet test start\n\n");

  printf("\n*** Checking assignments, printing and array production ***\n");

  printf("\nAssigning {5} to first using assign_element\n");
  set__assign_element(&first, 5);
  print_set("First is", &first, elements);

  printf("\nAssigning {8,1,3} to second using assign_list\n");
  set__assign_list(&second, 8, 1, 3, SET__END);
  print_set("Second is", &second, elements);

  printf("\nAssigning second to first using assign_set: note set size change\n");
  set__assign_set(&first, &second);
  print_set("First is", &first, elements);

  printf("\nComparing first and second sets: result %i\n", set__compare(&first,&second));

  printf("\nAssigning {5} to first using assign_element\n");
  set__assign_element(&first, 5);
  print_set("First is", &first, elements);

  printf("\n*** Checking comparison ***\n");
  printf("\nComparing first and second sets: result %i\n", set__compare(&first,&second));

  printf("\n*** Checking inversion ***\n");
  printf("\nInverting first set against universe 16\n");
  set__invert(&first, 16);
  print_set("After inversion, first is", &first, elements);

  printf("\n*** Checking growing, minimim size, normalisation and freeing ***\n");
  printf("\nAssigning {5} to first using assign_element\n");
  set__assign_element(&first, 5);
  print_set("First is", &first, elements);

  printf("\nGrowing first set to size 4\n");
  set__grow(&first, 4);
  print_set("First is", &first, elements);

  printf("\nRenormalising set\n");
  set__normalise(&first);
  print_set("First is", &first, elements);

  printf("\nSetting minimum size to 5 and renormalising\n");
  set__minimum_size(5);
  set__normalise(&first);
  print_set("First is", &first, elements);

  printf("\nSetting minimum size to 0 and renormalising\n");
  set__minimum_size(0);
  set__normalise(&first);
  print_set("First is", &first, elements);

  printf("\nFreeing first\n");
  set__free(&first);
  print_set("First is", &first, elements);

  printf("\n*** Checking uniting ***\n");
  printf("\nUniting {5} into first using unite_element\n");
  set__unite_element(&first, 5);
  print_set("First is", &first, elements);

  printf("\nUniting {8,1,3} into first using unite_list\n");
  set__unite_list(&first, 8, 1, 3, SET__END);
  print_set("First is", &first, elements);

  printf("\nAssigning {9,1,16,2} to second using assign_list\n");
  set__assign_list(&second, 9, 1, 16, 2, SET__END);
  print_set("Second is", &second, elements);

  printf("\nUniting second into first using unite_set\n");
  set__unite_set(&first, &second);
  print_set("First is", &first, elements);

  printf("\n*** Checking differencing ***\n");
  printf("\nDifferencing {5} from first using difference_element\n");
  set__difference_element(&first, 5);
  print_set("First is", &first, elements);

  printf("\nDifferencing {14, 1, 13} from first using difference_list\n");
  set__difference_list(&first, 14, 1, 13, SET__END);
  print_set("First is", &first, elements);

  printf("\nAssigning {8,1,2} to second using assign_list\n");
  set__assign_list(&second, 8, 1, 2, SET__END);
  print_set("Second is", &second, elements);

  printf("\nDifferencing second from first using difference_set\n");
  set__difference_set(&first, &second);
  print_set("First is", &first, elements);

  printf("\n*** Checking inclusion ***\n");
  printf("\nAssigning {8,1,3,16} to first using assign_list\n");
  set__assign_list(&first, 8, 1, 3, 16, SET__END);
  print_set("First is", &first, elements);

  printf("\nChecking inclusion of {5} in first using includes_element: result %i\n", set__includes_element(&first, 5));
  printf("\nChecking inclusion of {16} in first using includes_element: result %i\n", set__includes_element(&first, 16));

  printf("\nChecking inclusion of {8,1,3} in first using includes_list: result %i\n", set__includes_list(&first, 8, 1, 3, SET__END));
  printf("\nChecking inclusion of {2,1,3} in first using includes_list: result %i\n", set__includes_list(&first, 8, 1, 2, SET__END));

  printf("\nAssigning {8,1,3} to second using assign_list\n");
  set__assign_list(&second, 8, 1, 3, SET__END);
  print_set("Second is", &second, elements);

  printf("\nChecking inclusion of second in first using includes_set: result %i\n", set__includes_set(&first, &second));

  printf("\nAssigning {2,1,3} to second using assign_list\n");
  set__assign_list(&second, 2, 1, 3, SET__END);
  print_set("Second is", &second, elements);

  printf("\nChecking inclusion of second in first using includes_set: result %i\n", set__includes_set(&first, &second));

  printf("\n*** Checking intersecting ***\n");
  printf("\nAssigning {8,1,3,16} to first using assign_list\n");
  set__assign_list(&first, 8, 1, 3, 16, SET__END);
  print_set("First is", &first, elements);

  printf("\nIntersecting {3} against first using intersect_element\n");
  set__intersect_element(&first, 3);
  print_set("First is", &first, elements);

  printf("\nIntersecting {16} against first using intersect_element\n");
  set__intersect_element(&first, 16);
  print_set("First is", &first, elements);

  printf("\nAssigning {8,1,13,16} to first using assign_list\n");
  set__assign_list(&first, 8, 1, 13, 16, SET__END);
  print_set("First is", &first, elements);

  printf("\nIntersecting {14, 1, 13} against first using intersect_list\n");
  set__intersect_list(&first, 14, 1, 13, SET__END);
  print_set("First is", &first, elements);

  printf("\nAssigning {8,1,3,16} to first using assign_list\n");
  set__assign_list(&first, 8, 1, 3, 16, SET__END);
  print_set("First is", &first, elements);

  printf("\nAssigning {8,1,3} to second using assign_list\n");
  set__assign_list(&second, 8, 1, 3, SET__END);
  print_set("Second is", &second, elements);

  printf("\nIntersecting second against first using intersect_set\n");
  set__intersect_set(&first, &second);
  print_set("First is", &first, elements);

  printf("\n\nSet test done\n\n");

  return 0;
}
