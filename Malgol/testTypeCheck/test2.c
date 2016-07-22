#include <stdio.h>

int max(int a, int b)
{
  if ((a > b))
    return a;
  else
    return b;
}

int arrayMax(int C[20])
{
  int i;
  int currentMax;
  i = 1;
  currentMax = C[0];
  while ((i < 20))
    {
      currentMax = max(currentMax, C[i]);
      i = (i + 1);
    }
  return currentMax;
}

int main()
{
  int A[20];
  int i;
  i = 0;
  while ((i < 20))
    {
      if (((i % 2) == 0))
        A[i] = (-i);
      else
        A[i] = i;
      i = (i + 1);
    }
  printf("%d\n", arrayMax(A));
  A[19] = 0;
  printf("%d\n", arrayMax(A));
  A[10] = 101;
  printf("%d\n", arrayMax(A));
  return 0;
}

