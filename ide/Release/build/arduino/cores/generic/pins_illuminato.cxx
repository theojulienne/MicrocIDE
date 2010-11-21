//************************************************************************


#ifndef _AVR_IO_H_
	#include <avr/io.h>
#endif
#ifndef Wiring_h
	#include "wiring.h"
#endif
#ifndef Pins_Arduino_h
	#include "pins_arduino.h"
#endif

#ifndef PA
	#define PA 1
	#define PB 2
	#define PC 3
	#define PD 4
	#define PE 5
	#define PF 6
	#define PG 7
#endif


const uint16_t PROGMEM port_to_mode_PGM[] = {
	NOT_A_PORT,
	&DDRA,
	&DDRB,
	&DDRC,
	&DDRD,
	&DDRE,
	&DDRF,
	&DDRG,
};

const uint16_t PROGMEM port_to_output_PGM[] = {
	NOT_A_PORT,
	&PORTA,
	&PORTB,
	&PORTC,
	&PORTD,
	&PORTE,
	&PORTF,
	&PORTG,
};

const uint16_t PROGMEM port_to_input_PGM[] = {
	NOT_A_PIN,
	&PINA,
	&PINB,
	&PINC,
	&PIND,
	&PINE,
	&PINF,
	&PING,
};


const uint8_t PROGMEM digital_pin_to_port_PGM[] =
{
	PE,		//*	PE 0	**	0	USART0_RX
	PE,		//*	PE 1	**	1	USART0_TX
	PE,		//*	PE 2	**	2
	PE,		//*	PE 3	**	3
	PE,		//*	PE 4	**	4
	PE,		//*	PE 5	**	5
	PE,		//*	PE 6	**	6
	PE,		//*	PE 7	**	7

	PD,		//*	PD 2	**	8
	PD,		//*	PD 3	**	9
	PD,		//*	PD 4	**	10
	PD,		//*	PD 6	**	11
	PD,		//*	PD 7	**	12

	PG,		//*	PG 0	**	13

	PB,		//*	PB 0	**	14
	PB,		//*	PB 4	**	15
	PB,		//*	PB 5	**	16
	PB,		//*	PB 6	**	17

	PG,		//*	PG 3	**	18
	PG,		//*	PG 4	**	19

	PD,		//*	PD 0	**	20
	PD,		//*	PD 1	**	21

	PG,		//*	PG 1	**	22

	PC,		//*	PC 0	**	23
	PC,		//*	PC 1	**	24
	PC,		//*	PC 2	**	25
	PC,		//*	PC 3	**	26
	PC,		//*	PC 4	**	27
	PC,		//*	PC 5	**	28
	PC,		//*	PC 6	**	29

	PA,		//*	PA 4	**	30
	PA,		//*	PA 5	**	31
	PA,		//*	PA 6	**	32
	PA,		//*	PA 7	**	33
	PG,		//*	PG 2	**	34
	PC,		//*	PC 7	**	35

	PF,		//*	PF 0	**	36
	PF,		//*	PF 1	**	37
	PF,		//*	PF 2	**	38
	PF,		//*	PF 3	**	39
	PF,		//*	PF 4	**	40
	PF,		//*	PF 5	**	41

	PB		//*	PB 7	**	42

};

const uint8_t PROGMEM digital_pin_to_bit_mask_PGM[] =
{
    /* 0 */		_BV(0),
    /* 1 */		_BV(1),
    /* 2 */		_BV(2),
    /* 3 */		_BV(3),
    /* 4 */		_BV(4),
    /* 5 */		_BV(5),
    /* 6 */		_BV(6),
    /* 7 */		_BV(7),

    /* 8 */		_BV(2),
    /* 9 */		_BV(3),
    /* 10 */	_BV(4),
    /* 11 */	_BV(6),
    /* 12 */	_BV(7),

    /* 13 */	_BV(0),

    /* 14 */	_BV(0),
    /* 15 */	_BV(4),
    /* 16 */	_BV(5),
    /* 17 */	_BV(6),

    /* 18 */	_BV(3),
    /* 19 */	_BV(4),

    /* 20 */	_BV(0),
    /* 21 */	_BV(1),

    /* 22 */	_BV(1),

    /* 23 */	_BV(0),
    /* 24 */	_BV(1),
    /* 25 */	_BV(2),
    /* 26 */	_BV(3),
    /* 27 */	_BV(4),
    /* 28 */	_BV(5),
    /* 29 */	_BV(6),
    /* 30 */	_BV(4),
    /* 31 */	_BV(5),
    /* 32 */	_BV(6),
    /* 33 */	_BV(7),
    /* 34 */	_BV(2),
    /* 35 */	_BV(7),

    /* 36 */	_BV(0),
    /* 37 */	_BV(1),
    /* 38 */	_BV(2),
    /* 39 */	_BV(3),
    /* 40 */	_BV(4),
    /* 41 */	_BV(5),

    /* 42 */	_BV(7),

};

const uint8_t PROGMEM digital_pin_to_timer_PGM[] = {

    /* 0 */  NOT_ON_TIMER,	//*	PE 0	**	0	USART0_RX
    /* 1 */  NOT_ON_TIMER,	//*	PE 1	**	1	USART0_TX
    /* 2 */  NOT_ON_TIMER,	//*	PE 2	**	2
    /* 3 */  NOT_ON_TIMER,	//*	PE 3	**	3
    /* 4 */  NOT_ON_TIMER,	//*	PE 4	**	4
    /* 5 */  NOT_ON_TIMER,	//*	PE 5	**	5
    /* 6 */  NOT_ON_TIMER,	//*	PE 6	**	6
    /* 7 */  NOT_ON_TIMER,	//*	PE 7	**	7

    /* 8 */  NOT_ON_TIMER,	//*	PD 2	**	8
    /* 9 */  NOT_ON_TIMER,	//*	PD 3	**	9
    /* 10 */ NOT_ON_TIMER,	//*	PD 4	**	10
    /* 11 */ NOT_ON_TIMER,	//*	PD 6	**	11
    /* 12 */ NOT_ON_TIMER,	//*	PD 7	**	12

    /* 13 */ NOT_ON_TIMER,	//*	PG 0	**	13

    /* 14 */ NOT_ON_TIMER,	//*	PB 0	**	14
    /* 15 */ TIMER0A,		//*	PB 4	**	15
    /* 16 */ TIMER1A,		//*	PB 5	**	16
    /* 17 */ TIMER1B,		//*	PB 6	**	17

    /* 18 */ NOT_ON_TIMER,	//*	PG 3	**	18
    /* 19 */ NOT_ON_TIMER,	//*	PG 4	**	19
	
    /* 20 */ NOT_ON_TIMER,	//*	PD 0	**	20
    /* 21 */ NOT_ON_TIMER,	//*	PD 1	**	21
	
    /* 22 */ NOT_ON_TIMER,	//*	PG 1	**	22
    /* 23 */ NOT_ON_TIMER,	//*	PC 0	**	23
    /* 24 */ NOT_ON_TIMER,	//*	PC 1	**	24
    /* 25 */ NOT_ON_TIMER,	//*	PC 2	**	25
    /* 26 */ NOT_ON_TIMER,	//*	PC 3	**	26
    /* 27 */ NOT_ON_TIMER,	//*	PC 4	**	27
    /* 28 */ NOT_ON_TIMER,	//*	PC 5	**	28
    /* 29 */ NOT_ON_TIMER,	//*	PC 6	**	29
    /* 30 */ NOT_ON_TIMER,	//*	PA 4	**	30
    /* 31 */ NOT_ON_TIMER,	//*	PA 5	**	31
    /* 32 */ NOT_ON_TIMER,	//*	PA 6	**	32
    /* 33 */ NOT_ON_TIMER,	//*	PA 7	**	33
    /* 34 */ NOT_ON_TIMER,	//*	PG 2	**	34
    /* 35 */ NOT_ON_TIMER,	//*	PC 7	**	35

    /* 36 */ NOT_ON_TIMER,	//*	PF 0	**	36
    /* 37 */ NOT_ON_TIMER,	//*	PF 1	**	37
    /* 38 */ NOT_ON_TIMER,	//*	PF 2	**	38
    /* 39 */ NOT_ON_TIMER,	//*	PF 3	**	39
    /* 40 */ NOT_ON_TIMER,	//*	PF 4	**	40
    /* 41 */ NOT_ON_TIMER,	//*	PF 5	**	41

    /* 42 */ TIMER2A,		//*	PB 7	**	42
};

