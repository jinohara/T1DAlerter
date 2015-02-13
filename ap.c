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
extern int getopt(int argc, char * const argv[], const char *optstring);

#define SOURCELEN 12	// number of BG values to consider for prediction
#define TARGETLEN 6	// number of BG values to predict
#define NUMBEROFNEIGHBORS 20	// use the 20 nearest neighbors to predict
#define MAXDIAMETER 100 * (400 * 400)	// max diameter

#define SQRT(n) (int) sqrt((double) n)

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
int *prediction;
int lflag, vflag;		// run time flags

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

// calculate eucidean distance between vectors of length veclen starting at v0 and v1
// actually returns the square
int
vdistance(v0, v1, veclen)
	int *v0, *v1;
	int veclen;
{	int i, dist, sumofsquares;

	sumofsquares = 0;
	for (i = 0; i < veclen; i++) {
		dist = v0[i] - v1[i];
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

	printf("at index %d\t", bgindex);
	for (i = 0; i < sourcelen; i++) 
		printf(" %3d", bgp[bgindex + i]);
	if (targetlen != 0) {
		printf(" |");
		for (i = 0; i < targetlen; i++)
			printf(" %3d", bgp[bgindex + sourcelen + i]);
	}
	printf("\n");
}

// calculate the diameter of the first numberofneighbors vectors
// when called with offset 0 and veclen sourcelen, calculates diameter of source ball
// when called with offset sourcelen and veclen targetlen, calculates diameter of target ball

int
vdiameter(offset, veclen)
	int offset;
	int veclen;
{	int maxdist, i, j, ijdistance;

	maxdist = -1;
	for (i = 0; i < numberofneighbors; i++)
		for (j = i + 1; j < numberofneighbors; j++) {
			ijdistance = vdistance(bgp + distvecp[i].index + offset, bgp + distvecp[j].index + offset, veclen);
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
		printf("distance %d at index %d\n", SQRT(distvecp[i].distance), distvecp[i].index);
}

// calculate the distance from thisvec to every other vector
// then look at the nearest numberofneighbors
// for each of these, calculate the diameter of the SOURCE and TARGET neighbor vectors
void
predict(thisvec)
	int thisvec;
{	int i, j, sourcediameter, targetdiameter, sum, preddist;

	// for each vector prior to thisvec, calculate distance to thisvec
	for (i = 0; i < thisvec; i++) {
		distvecp[i].index = i;
		distvecp[i].distance = vdistance(bgp + thisvec, bgp + i, sourcelen);
	}
	
	// sort the distances to pick off the nearest ones
	qsort(distvecp, thisvec, sizeof(struct distvec), dvcompar);

	if (vflag)
		debugalot(thisvec);

	// predicted values are the average for each time slot
	for (i = 0; i < targetlen; i++) {
		// average the i-th target value
		sum = 0;
		for (j = 0; j < numberofneighbors; j++) {
			sum += bgp[distvecp[j].index+sourcelen+i];
		}
		prediction[i] = sum/numberofneighbors;
	}

	// calculate the distance from the actual result to the predicted result
	preddist = vdistance(prediction, bgp + thisvec, targetlen);

	// calculate the diameter of the nearest neighbors and of their predictions.
	sourcediameter = vdiameter(0, sourcelen);
	targetdiameter = vdiameter(sourcelen, targetlen);

	// index, source diameter, target diameter
	printf("index %7d sdiam %3d tdiam %3d prediction", thisvec, SQRT(sourcediameter), SQRT(targetdiameter));
	for (i = 0; i < targetlen; i++)
		printf(" %3d", prediction[i]);
	printf(" actual");
	for (i = 0; i < targetlen; i++)
		printf(" %3d", bgp[thisvec+sourcelen+i]);
	printf(" distance %3d\n" , SQRT(preddist)/SQRT(targetlen));
}

void
usage(argv0, severity)
	char *argv0;
	int severity;
{
	if (severity)
		fprintf(stderr, "usage: %s [-lv] [-n numberofneighbor] [-s sourcelen] [-t targetlen] file\n", argv0);
	else
		printf("usage: %s [-lv] [-n numberofneighbor] [-s sourcelen] [-t targetlen] file\n", argv0);
	exit(severity);
}

int
main(argc, argv)
	int argc;
	char **argv;
{	int c, i, readcount;
	FILE *infile;
	char *argv0 = argv[0];

	sourcelen = SOURCELEN;
	targetlen = TARGETLEN;
	numberofneighbors = NUMBEROFNEIGHBORS;

	while ((c = getopt(argc, argv, "ln:s:t:v")) != -1) {
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
		case 'v':
			vflag = 1;
			break;
		case '?':
			usage(argv[0], 0);
		default:
			usage(argv[0], 1);
		}
	}
	argc -= optind;
	argv += optind;

	if (argc != 1)
		usage(argv0, 1);
	

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

	// alocate the result vector
	prediction = malloc(targetlen * sizeof(int));
	if (prediction == NULL) {
                fprintf(stderr, "prediction malloc(%ld) failed\n", targetlen * sizeof(int));
                exit(1);
        }

	if (lflag) {
		predict(bgcount - sourcelen - targetlen);	// predict for the most recent vector
	} else {
		for (i = numberofneighbors+1; i < bgcount - sourcelen - targetlen; i++)
			predict(i);		// predict all vectors
	}

	exit(0);
}

