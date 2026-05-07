#include <stdio.h>
#include <time.h>

#define ITERATIONS 10000000

void scalar(float a[], float b[], float c[]) {
    for(int i=0;i<4;i++)
        c[i] = a[i] * b[i];
}
void neon_asm(float *a, float *b, float *c) {
    asm volatile(
        "ldr q0, [%0]\n"
        "ldr q1, [%1]\n"
        "fmul v2.4s, v0.4s, v1.4s\n"
        "str q2, [%2]\n"
        :
        : "r"(a), "r"(b), "r"(c) 
        : "v0", "v1", "v2"         
    );
}

int main() {
    float a[4] = {1.0, 2.0, 3.0, 4.0};
    float b[4] = {5.0, 6.0, 7.0, 8.0};
    float c[4];

    clock_t start, end;
    double scalar_time, neon_time;

    start = clock();
    for(int i=0;i<ITERATIONS;i++)
        scalar(a,b,c);
    end = clock();
    scalar_time = (double)(end-start)/CLOCKS_PER_SEC;

    printf("Скалярное умножение:\n");
    for(int i=0;i<4;i++)
        printf("%f ", c[i]);
    printf("\nВремя простого умножения%f сек\n\n", scalar_time);

    start = clock();
    for(int i=0;i<ITERATIONS;i++)
        neon_asm(a,b,c);
    end = clock();
    neon_time = (double)(end-start)/CLOCKS_PER_SEC;

    printf("Результат SIMD:\n");
    for(int i=0;i<4;i++)
        printf("%f ", c[i]);
    printf("\nВремя SIMD умножения: %f сек\n\n", neon_time);

    printf("Ускорение: %fx\n", scalar_time / neon_time);

    return 0;
}
