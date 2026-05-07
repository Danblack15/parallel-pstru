// bench_pthreads.c
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <math.h>
#include <time.h>
#include <sys/time.h>

typedef struct {
    int id;
    long iter_count;
    volatile double *out;
} targs;

pthread_mutex_t log_lock = PTHREAD_MUTEX_INITIALIZER;

void *worker(void *param) {
    targs *p = (targs*)param;
    volatile double acc = 0.0;

    pthread_mutex_lock(&log_lock);
    fprintf(stdout, "[Thread #%d] Start processing\n", p->id);
    pthread_mutex_unlock(&log_lock);

    for (long k = 0; k < p->iter_count; ++k) {
        acc += sqrt((double)k * 0.5 + 1.0);
    }
    *(p->out) = acc;

    pthread_mutex_lock(&log_lock);
    fprintf(stdout, "[Thread #%d] Done, result=%.6f\n", p->id, acc);
    pthread_mutex_unlock(&log_lock);

    return (void*)0;
}

static inline double now_sec(void) {
    struct timeval t;
    gettimeofday(&t, NULL);
    return (double)t.tv_sec + (double)t.tv_usec * 1e-6;
}

double exec_parallel(int n_threads, long total_iter) {
    pthread_t *tid = malloc(n_threads * sizeof(pthread_t));
    targs *targ = malloc(n_threads * sizeof(targs));
    volatile double *res = calloc(n_threads, sizeof(double));
    long chunk = total_iter / n_threads;

    double t0 = now_sec();

    for (int i = 0; i < n_threads; ++i) {
        targ[i].id = i;
        targ[i].iter_count = chunk;
        targ[i].out = &res[i];
        if (pthread_create(&tid[i], NULL, worker, &targ[i]) != 0) {
            perror("pthread_create");
            exit(EXIT_FAILURE);
        }
    }

    for (int i = 0; i < n_threads; ++i) {
        pthread_join(tid[i], NULL);
    }

    double t1 = now_sec();

    free(tid);
    free(targ);
    free((void*)res);

    return t1 - t0;
}

double exec_sequential(long total_iter) {
    volatile double acc = 0.0;
    double t0 = now_sec();

    for (long k = 0; k < total_iter; ++k) {
        acc += sqrt((double)k * 0.5 + 1.0);
    }

    double t1 = now_sec();
    return t1 - t0;
}

int main(int argc, char *argv[]) {

    int n_threads = atoi(argv[1]);
    long total_iter = atol(argv[2]);

    double t_seq = exec_sequential(total_iter);
    printf("Sequential time: %.4f sec\n\n", t_seq);

    double t_par = exec_parallel(n_threads, total_iter);
    printf("Parallel time:   %.4f sec\n\n", t_par);

    printf("Speedup:      %.2fx\n", t_seq / t_par);
    printf("Efficiency:   %.1f%%\n", (t_seq / t_par / n_threads) * 100.0);

    pthread_mutex_destroy(&log_lock);
    return EXIT_SUCCESS;
}
