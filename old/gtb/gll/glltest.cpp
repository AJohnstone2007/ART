/******************************************************************************
*
* GLL parser generated Feb 25 2008 09:18:43 by GTB V2.41 from gll_paper_ex.gtb
*
******************************************************************************/

#include <stdlib.h>
#include <string.h>
#include "set.h"
#include "textio.h"
#include "gtb_gll.h"
enum gll_symbol{ GLL_P_SHREIKIllegal, GLL_P_HASH, GLL_P_DOLLAR, GLL_T_a, GLL_T_b, GLL_T_d, GLL_N_B, GLL_N_C, GLL_N_S, };

enum gll_label { 
  GLL_L_ILLEGAL, GLL_L_S_0,
  GLL_R_C__2__1, GLL_R_C__2__2, GLL_R_S__2__1, 
  GLL_L_B__1, GLL_L_B__2, GLL_L_C__1, GLL_L_C__2, GLL_L_C__3, GLL_L_S__1, GLL_L_S__2, 
  GLL_L_B, GLL_L_C, GLL_L_S,  };
int i;
const unsigned m = 4;
gll_symbol I[m] = { (gll_symbol)4, (gll_symbol)4, (gll_symbol)3, (gll_symbol)2, };

static set_ gll_first_S = SET_NULL;
static set_ gll_first_B__1 = SET_NULL;
static set_ gll_first_B__2 = SET_NULL;
static set_ gll_first_C__1 = SET_NULL;
static set_ gll_first_C__2 = SET_NULL;
static set_ gll_instance_first_C__2__1 = SET_NULL;
static set_ gll_instance_first_C__2__2 = SET_NULL;
static set_ gll_first_C__3 = SET_NULL;
static set_ gll_first_S__1 = SET_NULL;
static set_ gll_first_S__2 = SET_NULL;
static set_ gll_instance_first_S__2__1 = SET_NULL;

static void gll_set_init() {
  set_assign_list(&gll_first_S, GLL_T_a, GLL_T_b, GLL_T_d, SET_END);
  set_assign_list(&gll_first_B__1, GLL_T_a, GLL_T_b, SET_END);
  set_assign_list(&gll_first_B__2, GLL_T_a, SET_END);
  set_assign_list(&gll_first_C__1, GLL_T_b, SET_END);
  set_assign_list(&gll_first_C__2, GLL_T_a, GLL_T_b, SET_END);
  set_assign_list(&gll_instance_first_C__2__1, GLL_T_a, GLL_T_b, SET_END);
  set_assign_list(&gll_instance_first_C__2__2, GLL_T_a, GLL_T_b, SET_END);
  set_assign_list(&gll_first_C__3, GLL_T_b, SET_END);
  set_assign_list(&gll_first_S__1, GLL_T_d, SET_END);
  set_assign_list(&gll_first_S__2, GLL_T_a, GLL_T_b, SET_END);
  set_assign_list(&gll_instance_first_S__2__1, GLL_T_a, GLL_T_b, SET_END);
}

static void gll_recognise() {
  gss_node* c_u;
  i = 0;
  enum gll_label L = GLL_L_S;

  if (!set_includes_element(&gll_first_S, I[0])) { text_printf("Reject"); exit(1); }

  c_u = gll_init(GLL_L_S_0, 3, 7);
  while (1)
    switch(L){
      case GLL_L_S_0:
        if (R_cardinality != 0) { gll_remove(&L, &c_u, &i); break; } else gll_print_accept_or_reject();

 /* ----------------------------------------------- */

      case GLL_L_B:
        if (set_includes_element(&gll_first_B__1, I[i])) gll_add(GLL_L_B__1, c_u, i);
        if (set_includes_element(&gll_first_B__2, I[i])) gll_add(GLL_L_B__2, c_u, i);
        L = GLL_L_S_0; break;

      case GLL_L_B__1:
        /* epsilon */
        /* return */ gll_pop(c_u, i); L = GLL_L_S_0; break;

      case GLL_L_B__2:
        /* skip leading terminal */ i++;
        /* return */ gll_pop(c_u, i); L = GLL_L_S_0; break;

 /* ----------------------------------------------- */

      case GLL_L_C:
        if (set_includes_element(&gll_first_C__1, I[i])) gll_add(GLL_L_C__1, c_u, i);
        if (set_includes_element(&gll_first_C__2, I[i])) gll_add(GLL_L_C__2, c_u, i);
        if (set_includes_element(&gll_first_C__3, I[i])) gll_add(GLL_L_C__3, c_u, i);
        L = GLL_L_S_0; break;

      case GLL_L_C__1:
        /* skip leading terminal */ i++;
        /* return */ gll_pop(c_u, i); L = GLL_L_S_0; break;

      case GLL_L_C__2:
        /* call nonterminal B */ c_u = gll_create(GLL_R_C__2__1, c_u, i); L = GLL_L_B; break;
      case GLL_R_C__2__1:
        if (set_includes_element(&gll_instance_first_C__2__2, I[i])) {
          /* call nonterminal C */ c_u = gll_create(GLL_R_C__2__2, c_u, i);L = GLL_L_C; break; }
        else { L = GLL_L_S_0; break; }
      case GLL_R_C__2__2:
        /* test for terminal b */ if (I[i] == GLL_T_b) i++; else { L = GLL_L_S_0; break; }
        /* return */ gll_pop(c_u, i); L = GLL_L_S_0; break;

      case GLL_L_C__3:
        /* skip leading terminal */ i++;
        /* test for terminal b */ if (I[i] == GLL_T_b) i++; else { L = GLL_L_S_0; break; }
        /* return */ gll_pop(c_u, i); L = GLL_L_S_0; break;

 /* ----------------------------------------------- */

      case GLL_L_S:
        if (set_includes_element(&gll_first_S__1, I[i])){ L = GLL_L_S__1; break; }
        else if (set_includes_element(&gll_first_S__2, I[i])){ L = GLL_L_S__2; break; }
        text_printf("Fatal internal error: LL(1) rule reached end of body"); exit (1);

      case GLL_L_S__1:
        /* skip leading terminal */ i++;
        /* return */ gll_pop(c_u, i); L = GLL_L_S_0; break;

      case GLL_L_S__2:
        /* call nonterminal C */ c_u = gll_create(GLL_R_S__2__1, c_u, i); L = GLL_L_C; break;
      case GLL_R_S__2__1:
        /* test for terminal a */ if (I[i] == GLL_T_a) i++; else { L = GLL_L_S_0; break; }
        /* return */ gll_pop(c_u, i); L = GLL_L_S_0; break;

      default: text_printf("Fatal internal error: unknown label"); exit (1);
    };
}

int main() {
  gll_set_init();
  gll_recognise();
}
