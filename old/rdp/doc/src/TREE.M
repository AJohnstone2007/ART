int a, b = 3 + 4;
 
 a = a + b;
 
 print("a is ", a);
 
 begin 
  a = a + b;
  b = b - 1
 end;

 if a == 1 then a = 0;

 if a > b then a = 0 else a = 1;
 
 while a > b do
  begin
   print(a);
   a = a - 1
  end;
