/****************************************************************************
*
* GTB release 2.00 by Adrian Johnstone (A.Johnstone@rhbnc.ac.uk) 30 August 2004
*
* util.cpp - general utility functions
*
* This file may be freely distributed. Please mail improvements to the author.
*
*******************************************************************************/
int utl_iterative_bsearch_unsigned(unsigned *buffer, int low_index, int high_index, const unsigned target)
{
  while (1)
    if (low_index > high_index)
      return -1;
    else
    {
      int mid_index = (low_index + high_index) / 2;

      if (target == buffer[mid_index])
        return mid_index;
      else
      {
        if (target < buffer[mid_index])
          high_index = mid_index - 1;
        else
          low_index = mid_index + 1;
      }
    }
}

