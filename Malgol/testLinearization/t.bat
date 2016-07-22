@set CP=%CP%;..\bin
@java -ea -cp %CP% malgol.Main test%* > test%*.s
@gcc test%*.s -o test%*.exe
@test%*.exe
