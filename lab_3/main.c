#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <omp.h>  

volatile double sink = 0;  

void heavy_task_sequential(long start, long end) {
    volatile double local_sum = 0.0;
    for (long i = start; i < end; i++) {
        local_sum += sqrt((double)i * 0.5 + 1.0);
    }
    sink += local_sum;  
}

double run_sequential(long total_iterations) {
    sink = 0.0;
    double start_time = omp_get_wtime();
    
    heavy_task_sequential(0, total_iterations);
    
    return omp_get_wtime() - start_time;
}

double run_openmp(int thread_num, long total_iterations) {
    sink = 0.0;
    double start_time = omp_get_wtime();
    #pragma omp parallel for num_threads(thread_num) \
                             reduction(+:sink) \
                             schedule(static)
    for (long i = 0; i < total_iterations; i++) {
        sink += sqrt((double)i * 0.5 + 1.0);
    }
    
    return omp_get_wtime() - start_time;
}
#include <pthread.h>

typedef struct {
    long start;
    long end;
    volatile double *result;
} pthread_args_t;

void *pthread_worker(void *arg) {
    pthread_args_t *args = (pthread_args_t*)arg;
    volatile double local_sum = 0.0;
    
    for (long i = args->start; i < args->end; i++) {
        local_sum += sqrt((double)i * 0.5 + 1.0);
    }
    *(args->result) = local_sum;
    return NULL;
}

double run_pthreads(int thread_num, long total_iterations) {
    sink = 0.0;
    pthread_t *threads = malloc(thread_num * sizeof(pthread_t));
    pthread_args_t *args = malloc(thread_num * sizeof(pthread_args_t));
    volatile double *results = calloc(thread_num, sizeof(double));
    
    long chunk = total_iterations / thread_num;
    
    double start_time = omp_get_wtime();  
    
    for (int i = 0; i < thread_num; i++) {
        args[i].start = i * chunk;
        args[i].end = (i == thread_num - 1) ? total_iterations : (i + 1) * chunk;
        args[i].result = &results[i];
        pthread_create(&threads[i], NULL, pthread_worker, &args[i]);
    }
    
    for (int i = 0; i < thread_num; i++) {
        pthread_join(threads[i], NULL);
    }
    
    for (int i = 0; i < thread_num; i++) {
        sink += results[i];
    }
    
    double elapsed = omp_get_wtime() - start_time;
    
    free(threads);
    free(args);
    free((void*)results);
    return elapsed;
}

int main(int argc, char** argv) {    
    int thread_num = atoi(argv[1]);
    long total_iterations = atol(argv[2]);
    
    printf("Всего итераций: %ld\n", total_iterations);
    printf("Потоков: %d\n", thread_num);
    printf("Доступно ядер OpenMP: %d\n\n", omp_get_num_procs());
    
    printf("Последдователдьно...\n");
    double t_seq = run_sequential(total_iterations);
    printf("Время: %.4f sec\n\n", t_seq);
    
    printf("Pthreads (%d потоков)...\n", thread_num);
    double t_pthread = run_pthreads(thread_num, total_iterations);
    printf("Время: %.4f sec\n\n", t_pthread);
    
    printf("OpenMP (%d потоков)...\n", thread_num);
    double t_openmp = run_openmp(thread_num, total_iterations);
    printf("Время: %.4f sec\n\n", t_openmp);
    
    printf("Сравнение производительности:\n");
    printf("┌─────────────┬────────────┬────────────┬────────────┐\n");
    printf("│ Метод       │ Время (с)  │ Speedup    │ Efficiency │\n");
    printf("├─────────────┼────────────┼────────────┼────────────┤\n");
    printf("│ Sequential  │ %8.4f   │ %7.2f    │ %7.1f%%  │\n", t_seq, 1.0, 100.0);
    printf("│ Pthreads    │ %8.4f   │ %7.2f    │ %7.1f%%  │\n", t_pthread, t_seq/t_pthread, (t_seq/t_pthread/thread_num)*100);
    printf("│ OpenMP      │ %8.4f   │ %7.2f    │ %7.1f%%  │\n", t_openmp, t_seq/t_openmp, (t_seq/t_openmp/thread_num)*100);
    printf("└─────────────┴────────────┴────────────┴────────────┘\n");
    
    return EXIT_SUCCESS;
}
