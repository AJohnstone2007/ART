int a = 9, b = 12;

  if a == 0 then
    print("GCD is", b)
  else
  begin
    while b != 0 do
      if a>b then
        a = a - b
      else
        b = b - a;

    print("GCD is", a)
  end;
