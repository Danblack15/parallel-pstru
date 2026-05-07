	.section	__TEXT,__text,regular,pure_instructions
	.build_version macos, 15, 0	sdk_version 26, 2
	.globl	_scalar                         ; -- Begin function scalar
	.p2align	2
_scalar:                                ; @scalar
	.cfi_startproc
; %bb.0:
	sub	sp, sp, #32
	.cfi_def_cfa_offset 32
	str	x0, [sp, #24]
	str	x1, [sp, #16]
	str	x2, [sp, #8]
	str	wzr, [sp, #4]
	b	LBB0_1
LBB0_1:                                 ; =>This Inner Loop Header: Depth=1
	ldr	w8, [sp, #4]
	subs	w8, w8, #4
	b.ge	LBB0_4
	b	LBB0_2
LBB0_2:                                 ;   in Loop: Header=BB0_1 Depth=1
	ldr	x8, [sp, #24]
	ldrsw	x9, [sp, #4]
	ldr	s0, [x8, x9, lsl #2]
	ldr	x8, [sp, #16]
	ldrsw	x9, [sp, #4]
	ldr	s1, [x8, x9, lsl #2]
	fmul	s0, s0, s1
	ldr	x8, [sp, #8]
	ldrsw	x9, [sp, #4]
	str	s0, [x8, x9, lsl #2]
	b	LBB0_3
LBB0_3:                                 ;   in Loop: Header=BB0_1 Depth=1
	ldr	w8, [sp, #4]
	add	w8, w8, #1
	str	w8, [sp, #4]
	b	LBB0_1
LBB0_4:
	add	sp, sp, #32
	ret
	.cfi_endproc
                                        ; -- End function
	.globl	_neon_asm                       ; -- Begin function neon_asm
	.p2align	2
_neon_asm:                              ; @neon_asm
	.cfi_startproc
; %bb.0:
	sub	sp, sp, #32
	.cfi_def_cfa_offset 32
	str	x0, [sp, #24]
	str	x1, [sp, #16]
	str	x2, [sp, #8]
	ldr	x8, [sp, #24]
	ldr	x9, [sp, #16]
	ldr	x10, [sp, #8]
	; InlineAsm Start
	ldr	q0, [x8]
	ldr	q1, [x9]
	fmul.4s	v2, v0, v1
	str	q2, [x10]

	; InlineAsm End
	add	sp, sp, #32
	ret
	.cfi_endproc
                                        ; -- End function
	.globl	_main                           ; -- Begin function main
	.p2align	2
_main:                                  ; @main
	.cfi_startproc
; %bb.0:
	sub	sp, sp, #144
	stp	x29, x30, [sp, #128]            ; 16-byte Folded Spill
	add	x29, sp, #128
	.cfi_def_cfa w29, 16
	.cfi_offset w30, -8
	.cfi_offset w29, -16
	adrp	x8, ___stack_chk_guard@GOTPAGE
	ldr	x8, [x8, ___stack_chk_guard@GOTPAGEOFF]
	ldr	x8, [x8]
	stur	x8, [x29, #-8]
	str	wzr, [sp, #60]
	adrp	x8, l___const.main.a@PAGE
	add	x8, x8, l___const.main.a@PAGEOFF
	ldr	q0, [x8]
	stur	q0, [x29, #-32]
	adrp	x8, l___const.main.b@PAGE
	add	x8, x8, l___const.main.b@PAGEOFF
	ldr	q0, [x8]
	stur	q0, [x29, #-48]
	bl	_clock
	str	x0, [sp, #48]
	str	wzr, [sp, #20]
	b	LBB2_1
LBB2_1:                                 ; =>This Inner Loop Header: Depth=1
	ldr	w8, [sp, #20]
	mov	w9, #38528                      ; =0x9680
	movk	w9, #152, lsl #16
	subs	w8, w8, w9
	b.ge	LBB2_4
	b	LBB2_2
LBB2_2:                                 ;   in Loop: Header=BB2_1 Depth=1
	sub	x0, x29, #32
	sub	x1, x29, #48
	add	x2, sp, #64
	bl	_scalar
	b	LBB2_3
LBB2_3:                                 ;   in Loop: Header=BB2_1 Depth=1
	ldr	w8, [sp, #20]
	add	w8, w8, #1
	str	w8, [sp, #20]
	b	LBB2_1
LBB2_4:
	bl	_clock
	str	x0, [sp, #40]
	ldr	x8, [sp, #40]
	ldr	x9, [sp, #48]
	subs	x8, x8, x9
	ucvtf	d0, x8
	mov	x8, #145685290680320            ; =0x848000000000
	movk	x8, #16686, lsl #48
	fmov	d1, x8
	fdiv	d0, d0, d1
	str	d0, [sp, #32]
	adrp	x0, l_.str@PAGE
	add	x0, x0, l_.str@PAGEOFF
	bl	_printf
	str	wzr, [sp, #16]
	b	LBB2_5
LBB2_5:                                 ; =>This Inner Loop Header: Depth=1
	ldr	w8, [sp, #16]
	subs	w8, w8, #4
	b.ge	LBB2_8
	b	LBB2_6
LBB2_6:                                 ;   in Loop: Header=BB2_5 Depth=1
	ldrsw	x9, [sp, #16]
	add	x8, sp, #64
	ldr	s0, [x8, x9, lsl #2]
	fcvt	d0, s0
	mov	x8, sp
	str	d0, [x8]
	adrp	x0, l_.str.1@PAGE
	add	x0, x0, l_.str.1@PAGEOFF
	bl	_printf
	b	LBB2_7
LBB2_7:                                 ;   in Loop: Header=BB2_5 Depth=1
	ldr	w8, [sp, #16]
	add	w8, w8, #1
	str	w8, [sp, #16]
	b	LBB2_5
LBB2_8:
	ldr	d0, [sp, #32]
	mov	x8, sp
	str	d0, [x8]
	adrp	x0, l_.str.2@PAGE
	add	x0, x0, l_.str.2@PAGEOFF
	bl	_printf
	bl	_clock
	str	x0, [sp, #48]
	str	wzr, [sp, #12]
	b	LBB2_9
LBB2_9:                                 ; =>This Inner Loop Header: Depth=1
	ldr	w8, [sp, #12]
	mov	w9, #38528                      ; =0x9680
	movk	w9, #152, lsl #16
	subs	w8, w8, w9
	b.ge	LBB2_12
	b	LBB2_10
LBB2_10:                                ;   in Loop: Header=BB2_9 Depth=1
	sub	x0, x29, #32
	sub	x1, x29, #48
	add	x2, sp, #64
	bl	_neon_asm
	b	LBB2_11
LBB2_11:                                ;   in Loop: Header=BB2_9 Depth=1
	ldr	w8, [sp, #12]
	add	w8, w8, #1
	str	w8, [sp, #12]
	b	LBB2_9
LBB2_12:
	bl	_clock
	str	x0, [sp, #40]
	ldr	x8, [sp, #40]
	ldr	x9, [sp, #48]
	subs	x8, x8, x9
	ucvtf	d0, x8
	mov	x8, #145685290680320            ; =0x848000000000
	movk	x8, #16686, lsl #48
	fmov	d1, x8
	fdiv	d0, d0, d1
	str	d0, [sp, #24]
	adrp	x0, l_.str.3@PAGE
	add	x0, x0, l_.str.3@PAGEOFF
	bl	_printf
	str	wzr, [sp, #8]
	b	LBB2_13
LBB2_13:                                ; =>This Inner Loop Header: Depth=1
	ldr	w8, [sp, #8]
	subs	w8, w8, #4
	b.ge	LBB2_16
	b	LBB2_14
LBB2_14:                                ;   in Loop: Header=BB2_13 Depth=1
	ldrsw	x9, [sp, #8]
	add	x8, sp, #64
	ldr	s0, [x8, x9, lsl #2]
	fcvt	d0, s0
	mov	x8, sp
	str	d0, [x8]
	adrp	x0, l_.str.1@PAGE
	add	x0, x0, l_.str.1@PAGEOFF
	bl	_printf
	b	LBB2_15
LBB2_15:                                ;   in Loop: Header=BB2_13 Depth=1
	ldr	w8, [sp, #8]
	add	w8, w8, #1
	str	w8, [sp, #8]
	b	LBB2_13
LBB2_16:
	ldr	d0, [sp, #24]
	mov	x8, sp
	str	d0, [x8]
	adrp	x0, l_.str.4@PAGE
	add	x0, x0, l_.str.4@PAGEOFF
	bl	_printf
	ldr	d0, [sp, #32]
	ldr	d1, [sp, #24]
	fdiv	d0, d0, d1
	mov	x8, sp
	str	d0, [x8]
	adrp	x0, l_.str.5@PAGE
	add	x0, x0, l_.str.5@PAGEOFF
	bl	_printf
	ldur	x9, [x29, #-8]
	adrp	x8, ___stack_chk_guard@GOTPAGE
	ldr	x8, [x8, ___stack_chk_guard@GOTPAGEOFF]
	ldr	x8, [x8]
	subs	x8, x8, x9
	b.eq	LBB2_18
	b	LBB2_17
LBB2_17:
	bl	___stack_chk_fail
LBB2_18:
	mov	w0, #0                          ; =0x0
	ldp	x29, x30, [sp, #128]            ; 16-byte Folded Reload
	add	sp, sp, #144
	ret
	.cfi_endproc
                                        ; -- End function
	.section	__TEXT,__literal16,16byte_literals
	.p2align	2, 0x0                          ; @__const.main.a
l___const.main.a:
	.long	0x3f800000                      ; float 1
	.long	0x40000000                      ; float 2
	.long	0x40400000                      ; float 3
	.long	0x40800000                      ; float 4

	.p2align	2, 0x0                          ; @__const.main.b
l___const.main.b:
	.long	0x40a00000                      ; float 5
	.long	0x40c00000                      ; float 6
	.long	0x40e00000                      ; float 7
	.long	0x41000000                      ; float 8

	.section	__TEXT,__cstring,cstring_literals
l_.str:                                 ; @.str
	.asciz	"\320\241\320\272\320\260\320\273\321\217\321\200\320\275\320\276\320\265 \321\203\320\274\320\275\320\276\320\266\320\265\320\275\320\270\320\265:\n"

l_.str.1:                               ; @.str.1
	.asciz	"%f "

l_.str.2:                               ; @.str.2
	.asciz	"\n\320\222\321\200\320\265\320\274\321\217 \320\277\321\200\320\276\321\201\321\202\320\276\320\263\320\276 \321\203\320\274\320\275\320\276\320\266\320\265\320\275\320\270\321\217%f \321\201\320\265\320\272\n\n"

l_.str.3:                               ; @.str.3
	.asciz	"\320\240\320\265\320\267\321\203\320\273\321\214\321\202\320\260\321\202 SIMD:\n"

l_.str.4:                               ; @.str.4
	.asciz	"\n\320\222\321\200\320\265\320\274\321\217 SIMD \321\203\320\274\320\275\320\276\320\266\320\265\320\275\320\270\321\217: %f \321\201\320\265\320\272\n\n"

l_.str.5:                               ; @.str.5
	.asciz	"\320\243\321\201\320\272\320\276\321\200\320\265\320\275\320\270\320\265: %fx\n"

.subsections_via_symbols
