/*
  pins_arduino.c - pin definitions for the Arduino board
  Part of Arduino / Wiring Lite

  Copyright (c) 2005 David A. Mellis

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA

  $Id: pins_arduino.c 254 2007-04-20 23:17:38Z mellis $
*/

#ifndef _AVR_IO_H_
	#include <avr/io.h>
#endif
//#include "wiring_private.h"
#ifndef Wiring_h
	#include "wiring.h"
#endif
#ifndef Pins_Arduino_h
	#include "pins_arduino.h"
#endif


#define PA 1
#define PB 2
#define PC 3
#define PD 4

// these arrays map port names (e.g. port B) to the
// appropriate addresses for various functions (e.g. reading
// and writing)
const uint16_t PROGMEM port_to_mode_PGM[] = {
	NOT_A_PORT,
	&DDRA,
	&DDRB,
	&DDRC,
	&DDRD,
};

const uint16_t PROGMEM port_to_output_PGM[] = {
	NOT_A_PORT,
	&PORTA,
	&PORTB,
	&PORTC,
	&PORTD,
};

const uint16_t PROGMEM port_to_input_PGM[] = {
	NOT_A_PORT,
	&PINA,
	&PINB,
	&PINC,
	&PIND,
};

const uint8_t PROGMEM digital_pin_to_port_PGM[] = {
	PA,		//*	0	PA7	analog
	PA,		//*	1	PA6
	PA,		//*	2	PA5
	PA,		//*	3	PA4
	PA,		//*	4	PA3
	PA,		//*	5	PA2
	PA,		//*	6	PA1
	PA,		//*	7	PA0

	PB,		//*	8	PB0
	PB,		//*	9	PB1
	PB,		//*	10	PB2
	PB,		//*	11	PB3
	PB,		//*	12	PB4
	PB,		//*	13	PB5
	PB,		//*	14	PB6
	PB,		//*	15	PB7

	PC,		//*	16	PC0
	PC,		//*	17	PC1


	PD,		//*	18	PD2
	PD,		//*	19	PD3
	PD,		//*	20	PD4
	PD,		//*	21	PD5
	PD,		//*	22	PD6
	PD,		//*	23	PD7
	
	//*	these arnt brought out but are needed for access to LED
	PD,		//*	24	PD0
	PD,		//*	25	PD1

	PC,		//*	26	PC2
	PC,		//*	27	PC3
	PC,		//*	28	PC4
	PC,		//*	29	PC5
	PC,		//*	30	PC6		LED
	PC,		//*	31	PC7		LED
};

const uint8_t PROGMEM digital_pin_to_bit_mask_PGM[] = {
	_BV(7), /* 7, port A */
	_BV(6),
	_BV(5),
	_BV(4),
	_BV(3),
	_BV(2),
	_BV(1),
	_BV(0),

	_BV(0), /* 8, port B */
	_BV(1),
	_BV(2),
	_BV(3),
	_BV(4),
	_BV(5),
	_BV(6),
	_BV(7),

	_BV(0), /* 16, port C */
	_BV(1),

	_BV(2),	/* 18, port D */
	_BV(3),
	_BV(4),
	_BV(5),
	_BV(6),
	_BV(7),

	_BV(0), /*	port D */
	_BV(1),

	_BV(2),	/* 26, port C */
	_BV(3),
	_BV(4),
	_BV(5),
	_BV(6),
	_BV(7),
};

const uint8_t PROGMEM digital_pin_to_timer_PGM[] = {
	NOT_ON_TIMER,	//*	0 - PA7
	NOT_ON_TIMER,	//*	1 - PA6
	NOT_ON_TIMER,	//*	2 - PA5
	NOT_ON_TIMER,	//*	3 - PA4
	NOT_ON_TIMER,	//*	4 - PA3
	NOT_ON_TIMER,	//*	5 - PA2
	NOT_ON_TIMER,	//*	6 - PA1
	NOT_ON_TIMER,	//*	7 - PA0
	
	NOT_ON_TIMER,	//*	8 - PB0
	NOT_ON_TIMER,	//*	9 - PB1
	NOT_ON_TIMER,	//*	10 - PB2
	TIMER0A,		//*	11 - PB3 --- TIMER OC0
	NOT_ON_TIMER,	//*	12 - PB4
	NOT_ON_TIMER,	//*	13 - PB5
	NOT_ON_TIMER,	//*	14 - PB6
	NOT_ON_TIMER,	//*	15 - PB7
	
	NOT_ON_TIMER,	//*	16 - PC0
	NOT_ON_TIMER,	//*	17 - PC1
	
	NOT_ON_TIMER,	//*	18 - PD2
	NOT_ON_TIMER,	//*	19 - PD3
	TIMER1B,		//*	20 - PD4
	TIMER1A,		//*	21 - PD5
	NOT_ON_TIMER,	//*	22 - PD6
	TIMER2,			//*	23 - PD7
	
	//*	these arnt brought out but are needed for access to LED
	NOT_ON_TIMER,	//*	24 - PD0
	NOT_ON_TIMER,	//*	25 - PD1

	NOT_ON_TIMER,	//*	26 - PC2
	NOT_ON_TIMER,	//*	27 - PC3
	NOT_ON_TIMER,	//*	28 - PC4
	NOT_ON_TIMER,	//*	29 - PC5
	NOT_ON_TIMER,	//*	30 - PC6
	NOT_ON_TIMER,	//*	31 - PC7
};

