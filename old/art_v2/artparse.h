/*******************************************************************************
*
* Header file generated by RDP on Feb 23 2017 15:34:35 from artparse.bnf
*
*******************************************************************************/
#ifndef ARTPARSE_H
#define ARTPARSE_H

#include "arg.h"
#include "graph.h"
#include "hist.h"
#include "memalloc.h"
#include "scan.h"
#include "set.h"
#include "symbol.h"
#include "textio.h"


/* Maximum number of passes */
#define RDP_PASSES 1

/* Time and date stamp */
#define RDP_STAMP "Generated on Feb 23 2017 15:34:35 and compiled on " __DATE__ " at " __TIME__ 

/* Token enumeration */
enum
{
RDP_TT_BOTTOM = SCAN_P_TOP,
RDP_T_33 /* ! */ = SCAN_P_TOP,RDP_T_34 /* " */,RDP_T_35 /* # */,RDP_T_36 /* $ */,RDP_T_37 /* % */,RDP_T_3737 /* %% */,RDP_T_38 /* & */,RDP_T_39 /* ' */,
RDP_T_40 /* ( */,RDP_T_4042 /* (* */,RDP_T_41 /* ) */,RDP_T_42 /* * */,RDP_T_43 /* + */,RDP_T_44 /* , */,RDP_T_46 /* . */,RDP_T_4646 /* .. */,
RDP_T_47 /* / */,RDP_T_4747 /* // */,RDP_T_58 /* : */,RDP_T_5847 /* :/ */,RDP_T_584761 /* :/= */,RDP_T_5858 /* :: */,RDP_T_585861 /* ::= */,RDP_T_59 /* ; */,
RDP_T_60 /* < */,RDP_T_6045 /* <- */,RDP_T_604542 /* <-* */,RDP_T_6060 /* << */,RDP_T_606060 /* <<< */,RDP_T_6061 /* <= */,RDP_T_606142 /* <=* */,RDP_T_61 /* = */,
RDP_T_62 /* > */,RDP_T_6262 /* >> */,RDP_T_626262 /* >>> */,RDP_T_63 /* ? */,RDP_T_64 /* @ */,RDP_T_91 /* [ */,RDP_T_93 /* ] */,RDP_T_94 /* ^ */,
RDP_T_9494 /* ^^ */,RDP_T_9495 /* ^_ */,RDP_T_96 /* ` */,RDP_T_lexical,RDP_T_prelude,RDP_T_support,RDP_T_123 /* { */,RDP_T_124 /* | */,
RDP_T_126 /* ~ */,
RDP_TT_TOP
};

/* Tree data types */

typedef struct rdp_tree_node_data_struct
{
  SCAN_DATA
   struct rdp_tree_node_data_struct *thisNode; // a back pointer to ourselves so that insertions carry their trees within themselves
        enum artKind kind;                          // The flavour of this symbol - a copy is held in the tableEntry record if there is one
        enum artFold fold;                          // the TIF status from ^ and ^^ in the tree
        int nodeNumber;                             // intermediate form node number
        int instanceNumber;                         // instance number for this element in a production to generate attribute name
        const char* instanceName;                   // For a nonterminal with attributes or a names terminal, the instance name
                                                    // For named nonterminals and terminals the instance name is that name
                                                    // For unnamed nonterminals the instance name is node id concatentated with the instance number
        int deleted:1;                              // Nodes to ignore

        struct rdp_tree_node_data_struct *rightSibling;    // Direct link to the next right-sibling of this node. Null means there is no right-sibling

        struct symbols_data_node *tableEntry;       // Link to symbol table entry for this node as created in modularity code
        struct symbols_data_node *atomName;         // Link to symbol tale entry for the atom name given in a : expression in the RDT
        struct symbols_data_node *gatherName;       // the gather nonterminal name given in a ! expression in the RDT
        struct symbols_data_node *rangeUpperCharacterTerminal; // Top element of character enumeration

        struct rdp_tree_node_data_struct *tearLink; // for nodes labelled 'tear', a link back to the node that is to be inserted

        set_ first;                                 // First set of this node
        set_ follow;                                // Follow set for this node: FOLLOW() if LHS node; INSTANCE_FOLLOW() for RHS nodes
        set_ guard;                                 // Guard set for this node

        rdp_tree_node_data_struct *lhsL;            // Left Hand Side nonterminal rule (pos nodes only)
        rdp_tree_node_data_struct *niL;             // Nonterminal instance preceding slot (pos nodes only) - used in instanceOfs array
        rdp_tree_node_data_struct *aL;              // if EoA(L) then E_r_n else L
        rdp_tree_node_data_struct *nL;              // Slot after L
        rdp_tree_node_data_struct *pL;

        rdp_tree_node_data_struct *lrL;             // The L_r slot
        rdp_tree_node_data_struct *erL;             // The E_r slot

        bool isLHS;                                 // LHS node at level 2
        bool isDelayed;                             // Delay evaluation at this node
        bool isSPPFLabel;                           // Corresponds to an SPPF label in the generated parser
        bool isCodeLabel;                           // Corresponds to a GOTO label in the generated parser
        bool isTestRepeatLabel;                     // Corresponds to a testRepeat() label parameter in the generated parser
        bool isAltLabel;                            // Corresponds to a GLL_A_ style label used in the alternate template
        bool isRELabel;                             // Corresponds to a GLL_T_ style label used in the nullable bracket templates
        bool isReferredLabel;                       // Corresponds to a label that is used in the aL, pL
        bool isClosureLabel;                        // Corresponds to a label that is used in closure templates
        bool isGiftLabel;                           // Corresponds to an element that has a GIFT operator
        bool isPosSelector;                         // Is a POS node for which a selector set should be emitted
        bool isPosParentLabel;                      // Labels a POS node which has semantics or insertions below it
        bool isPopD;                                // Is popping descriptor
        bool isPostPredictivePop;                   // Is after deep end of FGLL do-first bracket
        bool isPredictivePop;                       // Is deep end of FGLL do-first bracket
        bool isNullableBracket;                     // bracket body matches epsilon OR bracket is )? OR bracket is )*
        bool isColon;                               // slot after a {}

        bool isEoOP;                                // is End of Optional or Parenthesis (pos nodes only)
        bool isEoA;                                 // is End of Alternate (pos nodes only)
        bool isEoR;                                 // is End of Rule (pos nodes only)
        bool isFiR;                                 // + is First in Rule (pos nodes only) - modified in Auguest 2016 EBNF paper
        bool isFiPC;                                // + is First in Positive Closure (pos nodes only) - added in Auguest 2016 EBNF paper

        bool isEoD;                                 // is End of Sequence under Do first
        bool isEoO;                                 // is End of Sequence under Optional
        bool isEoP;                                 // is End of Sequence under Positive Closure
        bool isEoK;                                 // is End of Sequence under Kleene Closure


} rdp_tree_node_data;
typedef struct rdp_tree_edge_data_struct
{
  int rdp_edge_kind;
   
} rdp_tree_edge_data;

/* Symbol table support */
/* Parser start production */
void text(rdp_tree_node_data* rdp_tree);



#endif

/* End of artparse.h */