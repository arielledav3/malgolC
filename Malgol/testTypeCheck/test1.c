#include <stdio.h>

int max2(int a, int b)
{
  if ((a > b))
    return a;
  else
    return b;
}

int max4(int a, int b, int c, int d)
{
  return max2(max2(a, b), max2(c, d));
}

int main()
{
  int i;
  int j;
  i = 10;
  j = 30;
  printf("%d\n", max4(i, 20, j, 40));
  printf("%d\n", max4(40, j, 20, i));
  printf("%d\n", max4(1, j, i, i));
  printf("%d\n", max4(i, i, i, i));
  return 0;
}

