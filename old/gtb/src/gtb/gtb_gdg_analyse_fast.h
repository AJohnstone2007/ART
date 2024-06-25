/*******************************************************************************
*
* GTB release 2.0 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 27 September 2005
*
* gtb_analyse_fast.h - high speed loop breaking for RIGLR terminalisation
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
#include "gtb_gdg.h"
void gdg_analyse_fast(gdg * this_gdg,
                        int break_set_disposition,
                        bool delete_break_sets,
                        int long break_set_cardinality_limit,
                        bool restart_cardinality_limit,
                        unsigned long break_set_count);
