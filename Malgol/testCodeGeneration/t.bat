@set CP=%CP%;..\bin
@java -ea -cp %CP% malgol.Main 5 test%* > test%*.s
@gcc test%*.s -o test%*.exe
@test%*.exe
