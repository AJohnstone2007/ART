atexp    ::=   scon                                 special constant
               〈op〉longvid                          value identifier
               { 〈exprow〉  }                        record
               # lab                                record selector
               ()                                   0-tuple
               (exp1  , ··· , expn)                 n-tuple, n ≥ 2
               [exp1  , ··· , expn]                 list, n ≥ 0
               (exp1  ; ··· ; expn)                 sequence, n ≥ 2
               let dec in exp1  ; ··· ; expn  end   local declaration, n ≥ 1
               ( exp )
exprow   ::=   lab = exp 〈 , exprow〉                expression row
appexp   ::=   atexp
               appexp atexp                         application expression
infexp   ::=   appexp
               infexp1  vid infexp2                 infix expression
exp      ::=   infexp
               exp : ty                             typed (L)
               exp1  andalso exp2                   conjunction
               exp1  orelse exp2                    disjunction
               exp handle match                     handle exception
               raise exp                            raise exception
               if exp1  then exp2  else exp3        conditional
               while exp1  do exp2                  iteration
               case exp of match                    case analysis
               fn match                             function
match    ::=   mrule 〈 | match〉
mrule    ::=   pat => exp

// Figure 20: Grammar: Expressions and Matches




dec                                  ::=   val tyvarseq valbind                                                 value declaration
                                           fun tyvarseq fvalbind                                                function declaration
                                           type typbind                                                         type declaration
                                                                       datatype datbind 〈withtype typbind〉      datatype declaration
                                                                       datatype tycon -=- datatype longtycon    datatype replication
                                                                       abstype datbind 〈withtype typbind〉       abstype declaration
                                           with dec end
                                           exception exbind                                                     exception declaration
                                           local dec1  in dec2  end                                             local declaration
                                                                       open longstrid1  ··· longstridn          open declaration, n ≥ 1
                                                                                                                empty declaration
                                           dec1  〈;〉 dec2                                                       sequential declaration
                                           infix 〈d〉 vid1  ··· vidn                                             infix (L) directive, n ≥ 1
                                           infixr 〈d〉 vid1  ··· vidn                                            infix (R) directive, n ≥ 1
                                           nonfix vid1  ··· vidn                                                nonfix directive, n ≥ 1
valbind                              ::=   pat = exp 〈and valbind〉
                                           rec valbind
fvalbind                             ::=                               〈op〉vid atpat11···atpat1n〈:ty〉=exp1      m, n ≥ 1
                                                                       |〈op〉vid atpat21···atpat2n〈:ty〉=exp2     See also note below
                                     |     ···                         ···
                                                                       |〈op〉vid atpatm1···atpatmn〈:ty〉=expm
                                                                       〈and fvalbind〉
typbind                              ::=                               tyvarseq tycon = ty 〈and typbind〉
datbind                              ::=                               tyvarseq tycon = conbind 〈and datbind〉
conbind                              ::=                               〈op〉vid 〈of ty〉 〈 | conbind〉
exbind                               ::=                               〈op〉vid 〈of ty〉 〈and exbind〉
〈op〉vid = 〈op〉longvid 〈and exbind〉

// Figure 21: Grammar: Declarations and Bindings



atpat    ::=                            wildcard
         scon                           special constant
         〈op〉longvid                    value identifier
         { 〈patrow〉  }                  record
         ()                             0-tuple
         (pat1  , ··· , patn)           n-tuple, n ≥ 2
         [pat1  , ··· , patn]           list, n ≥ 0
         ( pat )
patrow   ::=   ...                      wildcard
         lab = pat 〈 , patrow〉          pattern row
         vid〈:ty〉 〈as pat〉 〈, patrow〉   label as variable
pat      ::=   atpat                    atomic
         〈op〉longvid atpat              constructed value
         pat1  vid pat2                 constructed value (infix)
         pat : ty                       typed
         〈op〉vid〈: ty〉 as pat           layered

// Figure 22: Grammar: Patterns


ty      ::=   tyvar                                       type variable
        { 〈tyrow〉  }                                      record type expression
        tyseq longtycon                                   type construction
        ty1  * ··· * tyn                                  tuple type, n ≥ 2
        ty -> ty′                                         function type expression (R)
        ( ty )
tyrow   ::=   lab : ty 〈 , tyrow〉   type-expression row

Figure 23: Grammar: Type expressions
