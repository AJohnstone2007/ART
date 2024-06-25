/*******************************************************************************
*
* RDP release 1.50 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 20 December 1997
*
* mt_aux.c - Minitree multiple pass compiler semantic routines
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#include <stdarg.h>
#include <stdio.h>
#include <string.h>
#include "graph.h"
#include "memalloc.h"
#include "textio.h"
#include "minitree.h"
#include "ml_aux.h"
#include "mt_aux.h"

char * expression_walk(rdp_tree_node_data * root)
{
  /* Postorder expression walk */
  if (root->token == SCAN_P_ID)
    return root->id; 
  else if (root->token == SCAN_P_INTEGER)
  {
    char * result =(char *) mem_malloc(12); 
    
    sprintf(result, "#%lu", root->data.u); 
    return result; 
  }
  else
  {
    void * left_edge = graph_next_out_edge(root); 
    void * right_edge = graph_next_out_edge(left_edge); 
    
    char * left = expression_walk((rdp_tree_node_data *) graph_destination(left_edge)); 
    
    if (right_edge == NULL)   /* monadic operator */
    {
      char * dst = new_temporary(); 
      
      switch (root->token)
      {
        case RDP_T_45         /* - */ : emit("SUB", "-", dst, "0", left); break; 
        default: 
        text_message(TEXT_FATAL, "unexpected monadic operator found in expression walk: "
        "token number %i, identifier \'%s\'\n", root->token, root->id); 
      }
      return dst; 
    }
    else
    {
      char * right = expression_walk((rdp_tree_node_data *) graph_destination(right_edge)); 
      char * dst = new_temporary(); 
      
      switch (root->token)
      {
        case RDP_T_3361       /* != */ : emit("NE ", "!=", dst, left, right); break; 
        case RDP_T_42         /* * */ : emit("MUL", "*", dst, left, right); break; 
        case RDP_T_4242       /* ** */ : emit("EXP", "**", dst, left, right); break; 
        case RDP_T_43         /* + */ : emit("ADD", "+", dst, left, right); break; 
        case RDP_T_45         /* - */ : emit("SUB", "-", dst, left, right); break; 
        case RDP_T_47         /* / */ : emit("DIV", "/", dst, left, right); break; 
        case RDP_T_60         /* < */ : emit("LT ", "<", dst, left, right); break; 
        case RDP_T_6061       /* <= */ : emit("LE ", "<=", dst, left, right); break; 
        case RDP_T_6161       /* == */ : emit("EQ ", "==", dst, left, right); break; 
        case RDP_T_62         /* > */ : emit("GT ", ">", dst, left, right); break; 
        case RDP_T_6261       /* >= */ : emit("GE ", ">=", dst, left, right); break; 
        default: text_message(TEXT_FATAL, "unexpected diadic operator found in expression walk: "
        "token number %i, identifier \'%s\'\n", root->token, root->id); 
      }
      return dst; 
    }
  }
}

void tree_walk(rdp_tree_node_data * root)
{
  /* Preorder tree walk */
  if (root == NULL)
    return; 
  else
  {
    void * this_edge = graph_next_out_edge(root); 
    
    switch (root->token)
    {
      case 0:                 /* scan root or begin node's children */
      case RDP_T_begin: 
      {
        void * this_edge = graph_next_out_edge(root); 
        
        while (this_edge != NULL) /* walk children, printing results */
        {
          tree_walk((rdp_tree_node_data *) graph_destination(this_edge)); 
          this_edge = graph_next_out_edge(this_edge); 
        }
        break; 
      }
      
      case RDP_T_61           /* = */ : 
      emit("CPY", 
      "", 
      ((rdp_tree_node_data *) graph_destination(this_edge))->id, 
      expression_walk((rdp_tree_node_data *) graph_destination(graph_next_out_edge(this_edge))), NULL); 
      break; 
      
      case RDP_T_int: 
      {
        void * this_edge = graph_next_out_edge(root); 
        
        while (this_edge != NULL) /* walk children, declaring each variable */
        {
          void * child_edge; 
          rdp_tree_node_data * this_node =(rdp_tree_node_data *) graph_destination(this_edge); 
          
          emitf(" \n DATA\n%s: WORD 1\n\n CODE\n", this_node->id); 
          if ((child_edge = graph_next_out_edge(this_node))!= NULL)
            emit("CPY", "", this_node->id, 
          expression_walk((rdp_tree_node_data *) graph_destination(child_edge)), NULL); 
          this_edge = graph_next_out_edge(this_edge); 
        }
        break; 
      }
      
      case RDP_T_print: 
      {
        void * this_edge = graph_next_out_edge(root); 
        
        while (this_edge != NULL) /* walk children, printing results */
        {
          rdp_tree_node_data * this_node =(rdp_tree_node_data *) graph_destination(this_edge); 
          
          if (this_node->token == RDP_T_34 /* " */)
            emit_print('S', this_node->id); 
          else
            emit_print('I', expression_walk(this_node)); 
          
          this_edge = graph_next_out_edge(this_edge); 
        }
      }
      break; 
      
      case RDP_T_if: 
      {
        char * relation; 
        rdp_tree_node_data
        * rel_stat =(rdp_tree_node_data *) graph_destination(this_edge), 
        * then_stat =(rdp_tree_node_data *) graph_destination(graph_next_out_edge(this_edge)), 
        * else_stat =(rdp_tree_node_data *) graph_destination(graph_next_out_edge(
        graph_next_out_edge(this_edge))); 
        
        integer label = new_label(); 
        emitf("__IF_%lu:\n", label); 
        relation = expression_walk(rel_stat); 
        emitf(" BEQ  %s,__ELSE_%lu\t;ifn %s go to __ELSE_%lu \n", relation, label, relation, label); 
        tree_walk(then_stat); 
        emitf(" BRA  __FI_%lu\t;go to __FI_%lu\n__ELSE_%lu:\n", label, label, label); 
        tree_walk(else_stat); 
        emitf("__FI_%lu:\n", label); 
        break; 
      }
      
      case RDP_T_while: 
      {
        char * relation; 
        rdp_tree_node_data
        * rel_stat =(rdp_tree_node_data *) graph_destination(this_edge), 
        * do_stat =(rdp_tree_node_data *) graph_destination(graph_next_out_edge(this_edge)); 
        
        integer label = new_label(); 
        emitf("__DO_%lu:\n", label); 
        relation = expression_walk(rel_stat); 
        emitf(" BEQ  %s,__OD_%lu\t;ifn %s go to __OD_%lu \n", relation, label, relation, label); 
        tree_walk(do_stat); 
        emitf(" BRA  __DO_%lu\t;go to __DO_%lu\n__OD_%lu:\n", label, label, label); 
        break; 
      }
      
      default: 
      text_message(TEXT_FATAL, "unexpected tree node found: "
      "token number %i, identifier \'%s\'\n", root->token, root->id); 
    }
  }
}

void code_generate(char * source, char * output, void * tree_root)
{
  emit_open(source, output); 
  tree_walk((rdp_tree_node_data *) graph_next_node(tree_root)); 
  emit_close(); 
}

/* End of mt_aux.c */

