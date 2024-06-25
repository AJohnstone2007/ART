/*******************************************************************************
*
* ART histograms (c) Adrian Johnstone 2013
*
*******************************************************************************/
#if !defined(GLLHIST_H)
#define GLLHIST_H
class GLLHistogram {
class histogramNode {
public:
  unsigned long bucket;
  unsigned long value;
  histogramNode *next;
};

histogramNode *base;

public:
GLLHistogram()
{
  base = new histogramNode;
  base->bucket = base->value = 0;

  base->next = new histogramNode;
  base->next->bucket = ULONG_MAX;
  base->next->value = 0;
  base->next->next = NULL;
}

void update(unsigned long value)
{
  histogramNode *currentHistogramNode = base;
  histogramNode *previousHistogramNode;
  histogramNode *newHistogramNode;

  do
  {
    if (currentHistogramNode->bucket == value)
    {
      currentHistogramNode->value++;
      return;
    }

    previousHistogramNode = currentHistogramNode;
    currentHistogramNode = currentHistogramNode->next;
  }
  while (currentHistogramNode->bucket <= value);

  newHistogramNode = new histogramNode;
  newHistogramNode->bucket = value;
  newHistogramNode->value = 1;
  newHistogramNode->next = currentHistogramNode;
  previousHistogramNode->next = newHistogramNode;
}

void print(FILE *f, bool lineBreak)
{
  unsigned long cardinality = weightedSumBuckets();

  histogramNode *currentHistogramNode = base;

  while (currentHistogramNode->next != NULL)
  {
    if (currentHistogramNode->bucket == 0)
      fprintf(f, "%lu:%lu", currentHistogramNode->bucket, currentHistogramNode->value);
    else
      fprintf(f, "%lu:%lu(%.2f%%)", currentHistogramNode->bucket, currentHistogramNode->value, (currentHistogramNode->bucket == 0 ? -1.0:(100 * (double) currentHistogramNode->value)) / (cardinality == 0 ? 1.0 : (double) cardinality));

    if (lineBreak)
      printf("\n");
    else
      printf(",");

    currentHistogramNode = currentHistogramNode->next;
  }
}

unsigned long bucketValue(unsigned long bucket)
{
  histogramNode *currentHistogramNode = base;

  while (currentHistogramNode->next != NULL && currentHistogramNode->bucket != bucket)
    currentHistogramNode = currentHistogramNode->next;

  return currentHistogramNode->bucket == bucket ? currentHistogramNode->value : 0;
}

unsigned long countNonemptyBuckets()
{
  histogramNode *currentHistogramNode = base;
  unsigned long buckets = 0;

  while (currentHistogramNode->next != NULL)
  {
    if (currentHistogramNode->value != 0)
      buckets++;

    currentHistogramNode = currentHistogramNode->next;
  }

  return buckets;
}

unsigned long countAllBuckets()
{
  histogramNode *currentHistogramNode = base;
  unsigned long buckets = 0;

  while (currentHistogramNode->next != NULL)
  {
    buckets++;

    currentHistogramNode = currentHistogramNode->next;
  }

  return buckets;
}

unsigned long sumBuckets()
{
  histogramNode *currentHistogramNode = base;
  unsigned long sum = 0;

  while (currentHistogramNode->next != NULL)
  {
    sum += currentHistogramNode->value;

    currentHistogramNode = currentHistogramNode->next;
  }

  return sum;
}

unsigned long sumBucketsFrom(int bucketBase)
{

  histogramNode *currentHistogramNode = base;
  unsigned long sum = 0;

  while (currentHistogramNode->next != NULL)
  {
    if (currentHistogramNode->bucket >= bucketBase)
      sum += currentHistogramNode->value;

    currentHistogramNode = currentHistogramNode->next;
  }

  return sum;
}

unsigned long weightedSumBuckets()
{
  if (base == NULL)
    return 0;

  histogramNode *currentHistogramNode = base;
  unsigned long sum = 0;

  while (currentHistogramNode->next != NULL)
  {
    sum += currentHistogramNode->bucket * currentHistogramNode->value;

    currentHistogramNode = currentHistogramNode->next;
  }

  return sum;
}

};
#endif
