(*****************************************************************************
*
* RDP release 1.30 by Adrian Johnstone (adrian@dcs.rhbnc.ac.uk) 28 Dec 1994
*
* test.pas - a piece of Pascal source to test the Pascal parser
*
* This file may be freely distributed. Please mail improvements to the author.
*
*****************************************************************************)

PROGRAM string2101(i,o,output);

CONST maxcode=127;

TYPE etype=(e_nof, e_eof, e_badchar, e_badstring, e_controlstring,
            e_2linestring, e_badescape, e_badoct, e_badhex,
            e_inline, e_dochar, e_dostring, e_donest);

VAR
 f,i,o: text;
 null,ch: char;
 id: PACKED ARRAY[1..32] OF char;
 temp1,temp2,commentlevel,temp,param,stringsize,bsize,lineno,idl: integer;
 echo,stringdone,verbose,changed: boolean;
 outfile,filename: string;
 codetable: ARRAY[0..maxcode] OF integer;

PROCEDURE error(e: etype);
 BEGIN
   IF e=e_nof THEN BEGIN writeln('No source file specified'); halt(1) END;
   write('string21: ',filename,', line ',lineno,': ');
   CASE e OF
    e_inline: writeln('Inline initialisation');
    e_dochar: writeln('Character constant');
    e_dostring: writeln('String constant');
    e_donest: writeln('Nested comment');
    e_eof: BEGIN
            write('unexpected end of file');
            IF commentlevel>0 THEN write(' whilst skipping comment');
            writeln;
            halt(1);
           END;
    e_badchar: writeln('bad character constant terminator');
    e_badstring: writeln('bad string constant terminator');
    e_controlstring: writeln('control code found in string or character constant');
    e_2linestring: writeln('string or character constant not terminated before end of line');
    e_badescape: writeln('illegal character escape sequence \',ch);
    e_badoct: writeln('bad octal digit "',ch,'"in character escape sequence');
    e_badhex: writeln('bad hex digit "',ch,'"in character escape sequence');
   END
 END;

PROCEDURE inch;
 BEGIN IF eof(i) THEN error(e_eof) ELSE read(i,ch) END;

PROCEDURE getch; {main scanning routine:
                  echo last character read, get another and skip comments}
 VAR messageissued: boolean;
 BEGIN
  messageissued:=false;
  REPEAT
   IF commentlevel=0 THEN messageissued:=false;
   IF echo AND (ch IN [' '..'~']) THEN write(o,ch);
   IF eoln(i) THEN BEGIN lineno:=lineno+1; IF echo THEN writeln(o); END;
   IF eof(i) THEN error(e_eof) ELSE read(i,ch);
   IF ch='{' THEN BEGIN IF (commentlevel<>0) AND NOT messageissued
                        THEN BEGIN error(e_donest); messageissued:=true END;
                        commentlevel:=commentlevel+1;
                  END;
   IF ch='}' THEN BEGIN commentlevel:=commentlevel-1;
                        IF commentlevel<>0 THEN BEGIN changed:=true; ch:=' ' END;
                  END;
  UNTIL commentlevel=0;
 END;

PROCEDURE chargetch; {secondary scanner for character strings:
                      no echo, expand escapes, don't recognise comments
                      special returns:
                      control codes converted to space, eoln " ' set stringdone flag
                      errors:
                      eoln->e_2linestring, control->e_controlstring,
                      illegal escape->e_badescape}
 VAR count,temp: integer;
 BEGIN
   IF eoln(i) THEN BEGIN error(e_2linestring); stringdone:=true END
   ELSE
   inch;
   IF NOT (ch IN [' '..'~']) THEN BEGIN error(e_controlstring); ch:=' ' END ELSE
   IF ch IN ['''','"'] THEN stringdone:=true ELSE
   IF ch='\' {escape character}
   THEN
    BEGIN
     inch;
     CASE ch OF
      'n': ch:=chr(10);
      't': ch:=chr(9);
      'v': ch:=chr(11);
      'b': ch:=chr(8);
      'r': ch:=chr(13);
      'f': ch:=chr(12);
      'a': ch:=chr(7);
      '\':;
      '?':;
      '''':;
      '"':;
      '0'..'7': BEGIN
                 temp:=0;
                 FOR count:=1 TO 3 DO
                  BEGIN
                   temp:=temp*8+ord(ch)-ord('0');
                   IF count<3 THEN
                    BEGIN inch; IF NOT (ch IN ['0'..'7']) THEN error(e_badoct) END;
                  END;
                 ch:=chr(temp);
                END;
      'x': BEGIN
            temp:=0; FOR count:=1 TO 2 DO
             BEGIN
              inch; temp:=temp*16;
              CASE ch OF
               '0'..'9': temp:=temp+ord(ch)-ord('0');
               'a'..'z': temp:=temp+ord(ch)-ord('a')+10;
               'A'..'Z': temp:=temp+ord(ch)-ord('A')+10;
               ELSE error(e_badhex);
              END;
             END;
            ch:=chr(temp)
           END;
      ELSE error(e_badescape)
     END
    END
 END;

PROCEDURE getid;
 BEGIN
  IF ch IN ['a'..'z','A'..'Z','_'] THEN
  BEGIN
   idl:=1; id[idl]:=ch; getch;
   WHILE ch IN ['a'..'z','A'..'Z','_','0'..'9'] DO
    BEGIN idl:=idl+1; id[idl]:=ch; getch END;
  END
 END;

PROCEDURE docharconstant;
 BEGIN
  changed:=true;
  IF verbose THEN error(e_dochar);
  chargetch;
  IF echo THEN write(o,codetable[ord(ch)]:1)
          ELSE write(f,codetable[ord(ch)]:1);
  chargetch;
  IF ch<>'''' THEN error(e_badchar); ch:=null; getch;
 END;


PROCEDURE dostringconstant;
 BEGIN
  changed:=true;
  IF verbose THEN error(e_dostring);
  stringdone:=false; stringsize:=1; {null at end of string}
  WHILE NOT stringdone DO
   BEGIN
    chargetch;
    IF NOT stringdone THEN {fudge for closing "}
    IF echo THEN write(o,codetable[ord(ch)]:1,',')
            ELSE write(f,codetable[ord(ch)]:1,',');
    stringsize:=stringsize+1
   END;
  IF echo THEN write(o,'0') ELSE write(f,'0');
  IF NOT (ch='"') THEN error(e_badstring); ch:=null; getch
 END;

PROCEDURE docommand;
VAR count: integer; fch: char;
 BEGIN
  getch; getid;
  IF idl=3 THEN
   IF ((id[1]='v') OR (id[1]='V')) AND
      ((id[2]='a') OR (id[2]='A')) AND
      ((id[3]='r') OR (id[3]='R')) THEN
    BEGIN
     WHILE ch<>';' DO {scan to end of statement}
      BEGIN
       IF ch IN ['A'..'Z','a'..'z','_'] THEN getid {always remember last id}
       ELSE
        IF ch=':' THEN {copy out initialisation code}
         BEGIN
          changed:=true; IF verbose THEN error(e_inline);
          echo:=false; bsize:=1; {quieten output file}
          rewrite(f); write(f,'.init ');
          FOR count:=1 TO idl DO write(f,id[count]);
          write(f,': ');
          WHILE ch<>';' DO {scan to end of statement}
           BEGIN
            getch; IF NOT(ch IN ['"','''']) THEN write(f,ch);
            IF ch=',' THEN bsize:=bsize+1
            ELSE IF ch='"' THEN BEGIN dostringconstant; bsize:=bsize-1+stringsize END
             ELSE IF ch='''' THEN docharconstant;
           END;
          write(o,'[',bsize:1,'];' ); {finish off the .VAR statment}
          close(f); reset(f);
          WHILE NOT eof(f) DO BEGIN read(f,fch); IF fch<>';' THEN write(o,fch) END;
          echo:=true;
         END
        ELSE getch;
      END;
    END
 END;

PROCEDURE openi(s: string);
 BEGIN
  assign(i,s); {$I-} reset(i); {$I+}
  IF ioresult<>0 THEN BEGIN writeln('Can''t find ',s); halt(1) END;
 END;

BEGIN
 changed:=false; commentlevel:=0; verbose:=false; filename:='';
 FOR temp:=0 TO maxcode DO codetable[temp]:=temp;
 IF paramcount<1 THEN
  BEGIN
   writeln('string21 - a string preprocessor for asm21');
   writeln('Adrian Johnstone 1990: this software may be freely distributed');
   writeln(' -r load ASCII code table with reverse ASCII');
   writeln(' -t <codefile> load ASCII code table from file <codefile>');
   writeln(' -v verbose commentary');
   writeln(' -c display ASCII code table');
   halt(1)
  END;
 param:=1;
 WHILE param<=paramcount DO
  BEGIN
   IF paramstr(param)='-v' THEN verbose:=true ELSE
   IF paramstr(param)='-r' THEN
    FOR temp:=0 TO maxcode DO codetable[temp]:=maxcode-temp
   ELSE
    IF paramstr(param)='-t' THEN
     BEGIN
      param:=param+1;
      openi(paramstr(param));
      FOR temp:=0 TO maxcode DO
       IF not eof(i) THEN readln(i,codetable[temp]);
      close(i);
     END
    ELSE
     IF paramstr(param)='-c' THEN
      BEGIN
       writeln('Code table (decimal)');
       temp:=0;
       FOR temp1:=1 TO 16 DO
        BEGIN
         FOR temp2:=1 TO 8 DO
          BEGIN
           ch:=chr(temp); IF ch IN [' '..'~'] THEN write(ch) ELSE write(' ');
           write(':',temp:3,'    '); temp:=temp+1;
          END;
         writeln;
        END
      END ELSE
     filename:=paramstr(param);
   param:=param+1;
  END;
 IF filename='' THEN error(e_nof);
 IF verbose THEN writeln('Source file:   ',filename); openi(filename);
 outfile:=filename;
 delete(outfile,pos('.',outfile),100); outfile:=concat(outfile,'.dsp');
 IF verbose THEN writeln('Expanded file: ',outfile);  assign(o,outfile);
 rewrite(o);
 assign(f,'asmppp.tmp');
 null:=chr(0); lineno:=1;
 IF NOT eof(i) THEN read(i,ch); echo:=true;
 WHILE NOT eof(i) DO
  BEGIN
   IF ch='''' THEN docharconstant ELSE
    IF ch='"' THEN dostringconstant ELSE
     IF ch='.' THEN docommand
       ELSE getch {skip any other characters}
  END;
 close(i); close(o);
 IF verbose AND NOT changed THEN writeln('No changes made');
END.

{ End of test.pas }
