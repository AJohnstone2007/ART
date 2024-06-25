###############################################################################
#
# RDP release 1.60 by Adrian Johnstone (A.Johnstone@rhul.ac.uk) 12 September 2004
#
###############################################################################
# Configuration for g++ on Unix
CC      = g++
OBJ     = .o
EXE     =
DIFF    = diff -s
RM      = rm
CP      = cp
RDP_DIR = 
SUPP_DIR = rdp_supp/
CFLAGS = -I$(SUPP_DIR) -D_POSIX_SOURCE -Wmissing-prototypes -Wstrict-prototypes -fno-common -Wall -ansi -pedantic -g
LINK    = $(CC) -o ./
MATHS   = -lm
HERE    = ./
OBJ_ONLY = -c
# End of g++ on Unix

# Configuration for Borland C++ 5.0
#CC      = bcc32
#OBJ     = .obj
#EXE     = .exe
#DIFF    = fc
#RM      = del
#CP      = copy
#SUPP_DIR = rdp_supp/
#CFLAGS = -I$(SUPP_DIR) -A -c -P -w
#LINK    = $(CC) -e
#MATHS   = 
#HERE    = 
#OBJ_ONLY = -c
# End of Borland C++ 5.0

###############################################################################
#
# End of configuration : don't alter anything below this line
#
###############################################################################

# Standard macros

RDP_AUX = rdp_aux$(OBJ) rdp_gram$(OBJ) rdp_prnt$(OBJ)
RDP_SUPP = arg$(OBJ) graph$(OBJ) memalloc$(OBJ) scan$(OBJ) scanner$(OBJ) set$(OBJ) symbol$(OBJ) textio$(OBJ)
RDP_OBJ = rdp_*$(OBJ)

# Standard suffix rules

.c$(OBJ):
	$(CC) $(CFLAGS) $(OBJ_ONLY) $*.c

# default target: do everything

all: ms_test m_test mc_test ml_test mt_test pas_test pr_c_test rdp_test

# Build the support modules: do this locally in case user wants debug options
arg$(OBJ): $(SUPP_DIR)arg.h $(SUPP_DIR)arg.c
	$(CC) $(CFLAGS) $(OBJ_ONLY) $(SUPP_DIR)arg.c

graph$(OBJ): $(SUPP_DIR)graph.h $(SUPP_DIR)graph.c
	$(CC) $(CFLAGS) $(OBJ_ONLY) $(SUPP_DIR)graph.c

memalloc$(OBJ): $(SUPP_DIR)memalloc.h $(SUPP_DIR)memalloc.c
	$(CC) $(CFLAGS) $(OBJ_ONLY) $(SUPP_DIR)memalloc.c

scan$(OBJ): $(SUPP_DIR)scan.h $(SUPP_DIR)scan.c
	$(CC) $(CFLAGS) $(OBJ_ONLY) $(SUPP_DIR)scan.c

scanner$(OBJ): $(SUPP_DIR)scan.h $(SUPP_DIR)scanner.c
	$(CC) $(CFLAGS) $(OBJ_ONLY) $(SUPP_DIR)scanner.c

set$(OBJ): $(SUPP_DIR)set.h $(SUPP_DIR)set.c
	$(CC) $(CFLAGS) $(OBJ_ONLY) $(SUPP_DIR)set.c

symbol$(OBJ): $(SUPP_DIR)symbol.h $(SUPP_DIR)symbol.c
	$(CC) $(CFLAGS) $(OBJ_ONLY) $(SUPP_DIR)symbol.c

textio$(OBJ): $(SUPP_DIR)textio.h $(SUPP_DIR)textio.c
	$(CC) $(CFLAGS) $(OBJ_ONLY) $(SUPP_DIR)textio.c

# compile distributed rdp
rdp$(EXE): rdp$(OBJ) $(RDP_AUX) $(RDP_SUPP)
	$(LINK)rdp$(EXE) rdp$(OBJ) $(RDP_OBJ) $(RDP_SUPP)

# generate and compile rdp1 from rdp.bnf
rdp1.c: rdp$(EXE) rdp.bnf
	$(HERE)rdp -F -ordp1 rdp

rdp1$(EXE): rdp1$(OBJ) $(RDP_AUX) $(RDP_SUPP)
	$(LINK)rdp1$(EXE) rdp1$(OBJ) $(RDP_OBJ) $(RDP_SUPP)

# run rdp.bnf through rdp1 to make rdp2 and diff to see if they are the same
rdp_test: rdp1$(EXE)
	$(CP) rdp1.c rdp2.c
	$(HERE)rdp1 -F -ordp1 rdp
	-$(DIFF) rdp1.c rdp2.c

# generate, compile and test the mini syntax checker
mini_syn.c: rdp$(EXE) mini_syn.bnf
	$(HERE)rdp -F -omini_syn mini_syn

mini_syn$(EXE): mini_syn$(OBJ) $(RDP_SUPP)
	$(LINK)mini_syn$(EXE) mini_syn$(OBJ) $(RDP_SUPP)

ms_test: mini_syn$(EXE) testcalc.m
	$(HERE)mini_syn testcalc

# generate, compile and test the mini interpreter
minicalc.c: rdp$(EXE) minicalc.bnf
	$(HERE)rdp -F -ominicalc minicalc

minicalc$(EXE): minicalc$(OBJ) $(RDP_SUPP)
	$(LINK)minicalc$(EXE) minicalc$(OBJ) $(RDP_SUPP) $(MATHS)

m_test: minicalc$(EXE) testcalc.m
	$(HERE)minicalc testcalc

# generate, compile and test the minicond interpreter
minicond.c: rdp$(EXE) minicond.bnf
	$(HERE)rdp -F -ominicond minicond

minicond$(EXE): minicond$(OBJ) $(RDP_SUPP)
	$(LINK)minicond$(EXE) minicond$(OBJ) $(RDP_SUPP) $(MATHS)

mc_test: minicond$(EXE) testcond.m
	$(HERE)minicond testcond

# generate and compile the miniloop compiler
miniloop.c: rdp$(EXE) miniloop.bnf 
	$(HERE)rdp -F -ominiloop miniloop

miniloop$(EXE): miniloop$(OBJ) ml_aux$(OBJ) $(RDP_SUPP)
	$(LINK)miniloop$(EXE) miniloop$(OBJ) ml_aux$(OBJ) $(RDP_SUPP) $(MATHS)

# generate and compile the minitree compiler
minitree.c: rdp$(EXE) minitree.bnf 
	$(HERE)rdp -F -ominitree minitree

minitree$(EXE): minitree$(OBJ) ml_aux$(OBJ) mt_aux$(OBJ) $(RDP_SUPP)
	$(LINK)minitree$(EXE) minitree$(OBJ) m*_aux$(OBJ) $(RDP_SUPP)

# generate and compile the mvmasm assembler
mvmasm.c: rdp$(EXE) mvmasm.bnf 
	$(HERE)rdp -F -omvmasm mvmasm

mvmasm$(EXE): mvmasm$(OBJ) mvm_aux$(OBJ) $(RDP_SUPP)
	$(LINK)mvmasm$(EXE) mvmasm$(OBJ) mvm_aux$(OBJ) $(RDP_SUPP) $(MATHS)

# compile the mvmsim simulator
mvmsim$(EXE): mvmsim$(OBJ) $(RDP_SUPP)
	$(LINK)mvmsim$(EXE) mvmsim$(OBJ) $(RDP_SUPP)

# test the single-pass compiler, assembler and simulator
ml_test: miniloop$(EXE) mvmasm$(EXE) mvmsim$(EXE) testloop.m
	$(HERE)miniloop -otestloop.mvm testloop
	$(HERE)mvmasm -otestloop.sim testloop
	$(HERE)mvmsim testloop.sim

# test the tree based compiler, assembler and simulator
mt_test: minitree$(EXE) mvmasm$(EXE) mvmsim$(EXE) testtree.m
	$(HERE)minitree -otesttree.mvm testtree
	$(HERE)mvmasm -otesttree.sim testtree
	$(HERE)mvmsim testtree.sim

# generate, compile and test the pascal parser
pascal.c: rdp$(EXE) pascal.bnf
# Need -F to override dangling else
	$(HERE)rdp -opascal -F pascal

pascal$(EXE): pascal$(OBJ) $(RDP_SUPP)
	$(LINK)pascal$(EXE) pascal$(OBJ) $(RDP_SUPP)

pas_test: pascal$(EXE) test.pas
	$(HERE)pascal test

# generate and compile the ANSI C pretty printer
pretty_c.c: rdp$(EXE) pretty_c.bnf
	$(HERE)rdp -opretty_c pretty_c

pretty_c$(EXE): pretty_c$(OBJ) pr_c_aux$(OBJ) $(RDP_SUPP)
	$(LINK)pretty_c$(EXE) pretty_c$(OBJ) pr_c_aux$(OBJ) $(RDP_SUPP)

pr_c_test: pretty_c$(EXE) test.c
	$(HERE)pretty_c test
	-$(DIFF) test.c test.bak
	-$(RM) test.bak

#general targets to build parser for grammar defined in GRAMMAR macro
parser: $(RDP_SUPP)
	$(HERE)rdp $(GRAMMAR)
	$(CC) $(CFLAGS) $(OBJ_ONLY) rdparser.c
	$(LINK)rdparser$(EXE) rdparser$(OBJ) $(RDP_SUPP) $(MATHS)
	$(HERE)rdparser -v -Vrdparser.vcg -l $(HERE)$(GRAMMAR).str

parserf: $(RDP_SUPP)
	$(HERE)rdp -F $(GRAMMAR)
	$(CC) $(CFLAGS) $(OBJ_ONLY) rdparser.c
	$(LINK)rdparser$(EXE) rdparser$(OBJ) $(RDP_SUPP) $(MATHS)
	$(HERE)rdparser -v -Vrdparser.vcg -l $(HERE)$(GRAMMAR).str
# get rid of intermediate files
clean:
	-$(RM) *$(OBJ)
	-$(RM) *.bak
	-$(RM) rdp1*
	-$(RM) rdp2*
	-$(RM) pascal.c
	-$(RM) pascal.h
	-$(RM) pretty_c.c
	-$(RM) pretty_c.h
	-$(RM) mini_syn.c
	-$(RM) mini_syn.h
	-$(RM) minicalc.c
	-$(RM) minicalc.h
	-$(RM) minicond.c
	-$(RM) minicond.h
	-$(RM) miniloop.c
	-$(RM) miniloop.h
	-$(RM) minitree.c
	-$(RM) minitree.h
	-$(RM) mvmasm.c
	-$(RM) mvmasm.h
	-$(RM) mvmasm.out
	-$(RM) testloop.mvm
	-$(RM) testloop.sim
	-$(RM) testtree.mvm
	-$(RM) testtree.sim
	-$(RM) minitree.mvm
	-$(RM) miniloop.mvm

# return to intial distribution state by deleting executables
veryclean: clean
	-$(RM) rdparser.c
	-$(RM) rdparser.h
	-$(RM) rdp$(EXE)
	-$(RM) rdparser$(EXE)
	-$(RM) pascal$(EXE)
	-$(RM) mini_syn$(EXE)
	-$(RM) minicalc$(EXE)
	-$(RM) minicond$(EXE)
	-$(RM) miniloop$(EXE)
	-$(RM) minitree$(EXE)
	-$(RM) mvmasm$(EXE)
	-$(RM) mvmsim$(EXE)
	-$(RM) pretty_c$(EXE)




