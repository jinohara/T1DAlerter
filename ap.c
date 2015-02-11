// use BG time series data as a predictor
// given SOURCELEN sequential values, estimate the next TARGETLEN values
// output format is three comma-separated integers:
// index, source diameter, target diameter

#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <assert.h>

extern char *optarg;
extern int optind;
extern int optopt;
extern int opterr;
extern int optreset;

int
getopt(int argc, char * const argv[], const char *optstring);

#define SOURCELEN 12	// number of BG values to consider for prediction
#define TARGETLEN 6	// number of BG values to predict
#define NUMBEROFNEIGHBORS 20	// use the 20 nearest neighbors to predict
#define MAXDIAMETER 100 * (400 * 400)	// max diameter

int sourcelen = SOURCELEN;
int targetlen = TARGETLEN;
int numberofneighbors = NUMBEROFNEIGHBORS;

struct distvec {
	int index;
	int distance;
};

// it's easier to make these global, rather than pass them around as args everywhere
int bgcount;			// number of BG values
int *bgp;			// array of BG values, length bgcount
int distveccount;		// number of target vectors
struct distvec *distvecp;	// target vectors: array of <index, distance> pairs

// count the number of lines in the input file
int
countbgs(file)
	FILE *file;
{	int c, linecount;

	rewind(file);
	linecount = 0;
	while ((c = fgetc(file)) != EOF)
		if (c == '\n')
			linecount++;
	return linecount;
}

// read the BG values into the external array *bgp
// TODO: read two values from each line: BG and time-of-day
int
readbg(infile)
	FILE *infile;
{	int i, c;

	rewind(infile);
	for (i = 0; i < bgcount; i++) {
		fscanf(infile, "%d", &bgp[i]);
		while ((c = fgetc(infile)) != EOF) {
			if (c == '\n')
				break;
		}
		if (c == EOF)
			break;
	}
	return i;
}

// calculate eucidean distance between vectors of length veclen starting at index v0 and index v1
// actually returns the square
int
vdistance(v0, v1, veclen)
	int v0, v1;
	int veclen;
{	int i, dist, sumofsquares;

	sumofsquares = 0;
	for (i = 0; i < veclen; i++) {
		dist = bgp[v0+i] - bgp[v1+i];
		sumofsquares += dist * dist;
	}
	return sumofsquares;
}

// for qsort
int
dvcompar(dvp1, dvp2)
	struct distvec *dvp1, *dvp2;
{
	return(dvp1->distance - dvp2->distance);
}

// print on stderr the vector starting at bgindex of length sourcelen+targetlen
// for debugging
void
printbgvector(bgindex, sourcelen, targetlen)
	int bgindex;
	int sourcelen;
	int targetlen;
{	int i;

	fprintf(stderr, "at index %d\t", bgindex);
	for (i = 0; i < sourcelen; i++) 
		fprintf(stderr, " %3d", bgp[bgindex + i]);
	if (targetlen != 0) {
		fprintf(stderr, " |");
		for (i = 0; i < targetlen; i++)
			fprintf(stderr, " %3d", bgp[bgindex + sourcelen + i]);
	}
	fprintf(stderr, "\n");
}

// calculate the diameter of the first numberofneighbors vectors
// when called with offset 0 and veclen sourcelen, calculates diameter of source ball
// when called with offset sourcelen and veclen targetlen, calculates diameter of target ball

int
vdiameter(thisvec, offset, veclen)
	int offset;
	int veclen;
{	int maxdist, i, j, ijdistance;

	maxdist = -1;
	for (i = 0; i < numberofneighbors; i++)
		for (j = i + 1; j < numberofneighbors; j++) {
			ijdistance = vdistance(distvecp[i].index + offset, distvecp[j].index + offset, veclen);
			if (ijdistance > maxdist)
				maxdist = ijdistance;
		}

	return maxdist;
}
	
void
debugalot(thisvec)
        int thisvec;
{	int i;

	printbgvector(thisvec, sourcelen, 0);
	for (i = 0; i < numberofneighbors; i++)
		printbgvector(distvecp[i].index, sourcelen, targetlen);

	for (i = 0; i < numberofneighbors; i++)
		fprintf(stderr, "distance %d at index %d\n", (int) sqrt((double) distvecp[i].distance), distvecp[i].index);
}

// calculate the distance from thisvec to every other vector
// then look at the nearest numberofneighbors
// for each of these, calculate the diameter of the SOURCE and TARGET neighbor vectors
void
predict(thisvec)
	int thisvec;
{	int i, sourcediameter, targetdiameter, sum, j;

	for (i = 0; i < distveccount; i++) {
		distvecp[i].index = i;
		distvecp[i].distance = vdistance(thisvec, i, sourcelen);
	}
	
	// don't include thisvec in nearest neighbors
	if (thisvec < distveccount) {
		assert(distvecp[thisvec].distance == 0);	// sanity check -- distance to self is zero
		distvecp[thisvec].distance = MAXDIAMETER;	// not any more ha ha
	}

	qsort(distvecp, distveccount, sizeof(struct distvec), dvcompar);

#if 0	// for debugging
	debugalot(thisvec);
#endif

	sourcediameter = vdiameter(thisvec, 0, sourcelen);
	targetdiameter = vdiameter(thisvec, sourcelen, targetlen);

	// index, source diameter, target diameter
	printf("%d,%d,%d", thisvec, (int) sqrt((double) sourcediameter), (int) sqrt((double) targetdiameter));
	// the predicted values
	for (i = 0; i < targetlen; i++) {
		// average the i-th target value
		sum = 0;
		for (j = 0; j < numberofneighbors; j++) {
			sum += bgp[distvecp[j].index+sourcelen+i];
		}
		printf(",%d", sum/numberofneighbors);
	}
	printf("\n");
}

void
usage(argv0)
	char *argv0;
{
	fprintf(stderr, "usage: %s [-l] [-n numberofneighbor] [-s sourcelen] [-t targetlen] file\n", argv0);
}

int
main(argc, argv)
	int argc;
	char **argv;
{	int c, i, readcount, lflag;
	FILE *infile;
	char *argv0 = argv[0];

	sourcelen = SOURCELEN;
	targetlen = TARGETLEN;
	numberofneighbors = NUMBEROFNEIGHBORS;

	while ((c = getopt(argc, argv, "ln:s:t:")) != -1) {
		switch(c) {
		case 'l':		// predict last entry only
			lflag = 1;
			break;
		case 'n':		// number of nearest neighbors; default is 20
			numberofneighbors = atoi(optarg);
			break;
		case 's':		// source vector length; default is 12
			sourcelen = atoi(optarg);
			break;
		case 't':		// target len; default is 6
			targetlen = atoi(optarg);
			break;
		case '?':
			usage(argv[0]);
			exit(0);
		default:
			usage(argv[0]);
			exit(1);
		}
	}
	argc -= optind;
	argv += optind;

	if (argc != 1) {
		usage(argv0);
		exit(1);
	}

	if (sourcelen < 2 || targetlen < 2) {
		fprintf(stderr, "source and target lengths must be greater than two\n");
		exit(1);
	}

	if ((infile = fopen(argv[0], "r")) == NULL) {
		perror(argv[0]);
		exit(1);
	}
	
	// count the number of lines in the input file
	bgcount = countbgs(infile);
	if (bgcount <= 0) {
		fprintf(stderr, "invalid file\n");
		exit(1);
	}
	if (bgcount < sourcelen + targetlen + numberofneighbors) {
		fprintf(stderr, "need at least %d BG values\n", sourcelen + targetlen + numberofneighbors);
		exit(1);
	}

	// allocate space to read the BG data
	bgp = (int *) malloc(bgcount * sizeof(int));
	if (bgp == NULL) {
		fprintf(stderr, "malloc(%ld) failed\n", bgcount * sizeof(int));
		exit(1);
	}

	// read the BG values
	readcount = readbg(infile);
	assert(bgcount == readcount);

	// allocate for the distance structs
	distveccount = bgcount - sourcelen - targetlen;
	distvecp = malloc(distveccount * sizeof(struct distvec));
	if (distvecp == NULL) {
		fprintf(stderr, "distvec malloc(%ld) failed\n", distveccount * sizeof(struct distvec));
		exit(1);
	}

	if (lflag) {
		predict(bgcount - sourcelen);	// predict for the most recent vector
	} else {
		for (i = 0; i < bgcount - sourcelen; i++)
			predict(i);		// predict all vectors
	}

	exit(0);
}

