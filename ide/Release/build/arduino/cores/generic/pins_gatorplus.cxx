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

/* On an ATmega324P, the pin mappings are as shown below. The
 * first 20 pins (D0-D19) are for compatibility with ATmega8
 * and ATmega168 Arduinos. Specifically, the timer pins
 * correspond as do the analog pin numbers to their digital
 * pin counterparts (e.g., AI0-->D14, etc.)
 *
 *      Analog Digital   PWM     Pin
 *     -------+-------+-------+-------
 *            |  D 0  |       |  PC0
 *            |  D 1  |       |  PC1
 *            |  D 2  |       |  PC2
 *            |  D 3  |TIMER2B|  PD6
 *            |  D 4  |       |  PC3
 *            |  D 5  |TIMER0B|  PB4
 *            |  D 6  |TIMER0A|  PB3
 *            |  D 7  |       |  PD2
 *            |  D 8  |       |  PD3
 *            |  D 9  |TIMER1A|  PD5
 *            |  D 10 |TIMER1B|  PD4
 *            |  D 11 |TIMER2A|  PD7
 *            |  D 12 |       |  PC4
 *            |  D 13 |       |  PC6
 *      AI 0  |  D 14 |       |  PA0
 *      AI 1  |  D 15 |       |  PA1
 *      AI 2  |  D 16 |       |  PA2
 *      AI 3  |  D 17 |       |  PA3
 *      AI 4  |  D 18 |       |  PA4
 *      AI 5  |  D 19 |       |  PA5
 *      AI 6  |  D 20 |       |  PA6
 *      AI 7  |  D 21 |       |  PA7
 *            |  D 22 |       |  PC5
 *            |  D 23 |       |  PB0
 *            |  D 24 |       |  PB1
 *            |  D 25 |       |  PB2
 *            |  D 26 |       |  PB5
 *            |  D 27 |       |  PB6
 *            |  D 28 |       |  PB7
 */

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
	PC, /* D0 : PC0 */
	PC, /* D1 : PC1 */
	PC, /* D2 : PC2 */
	PD, /* D3 : PD6 */
	PC, /* D4 : PC3 */
	PB, /* D5 : PB4 */
	PB, /* D6 : PB3 */
	PD, /* D7 : PD2 */
	PD, /* D8 : PD3 */
	PD, /* D9 : PD5 */
	PD, /* D10 : PD4 */
	PD, /* D11 : PD7 */
	PC, /* D12 : PC4 */
	PC, /* D13 : PC6 */
	PA, /* D14 : PA0 */
	PA, /* D15 : PA1 */
	PA, /* D16 : PA2 */
	PA, /* D17 : PA3 */
	PA, /* D18 : PA4 */
	PA, /* D19 : PA5 */
	PA, /* D20 : PA6 */
	PA, /* D21 : PA7 */
	PC, /* D22 : PC5 */
  PB, /* D23 : PB0 */
  PB, /* D24 : PB1 */
  PB, /* D25 : PB2 */
  PB, /* D26 : PB5 */
  PB, /* D27 : PB6 */
  PB  /* D28 : PB7 */
};

const uint8_t PROGMEM digital_pin_to_bit_mask_PGM[] = {
	_BV(0), /* D0 : PC0 */
	_BV(1), /* D1 : PC1 */
	_BV(2), /* D2 : PC2 */
	_BV(6), /* D3 : PD6 */
	_BV(3), /* D4 : PC3 */
	_BV(4), /* D5 : PB4 */
	_BV(3), /* D6 : PB3 */
	_BV(2), /* D7 : PD2 */
	_BV(3), /* D8 : PD3 */
	_BV(5), /* D9 : PD5 */
	_BV(4), /* D10 : PD4 */
	_BV(7), /* D11 : PD7 */
	_BV(4), /* D12 : PC4 */
	_BV(6), /* D13 : PC6 */
	_BV(0), /* D14 : PA0 */
	_BV(1), /* D15 : PA1 */
	_BV(2), /* D16 : PA2 */
	_BV(3), /* D17 : PA3 */
	_BV(4), /* D18 : PA4 */
	_BV(5), /* D19 : PA5 */
	_BV(6), /* D20 : PA6 */
	_BV(7), /* D21 : PA7 */
	_BV(5), /* D22 : PC5 */
	_BV(0), /* D23 : PB0 */
	_BV(1), /* D24 : PB1 */
	_BV(2), /* D25 : PB2 */
	_BV(5), /* D26 : PB5 */
	_BV(6), /* D27 : PB6 */
	_BV(7)  /* D28 : PB7 */
};

const uint8_t PROGMEM digital_pin_to_timer_PGM[] = {
	NOT_ON_TIMER, /* D0 : PC0 */
	NOT_ON_TIMER, /* D1 : PC1 */
	NOT_ON_TIMER, /* D2 : PC2 */
	TIMER2B,      /* D3 : PD6 (TIMER2B) */
	NOT_ON_TIMER, /* D4 : PC3 */
	TIMER0B,      /* D5 : PB4 (TIMER0B) */
	TIMER0A,      /* D6 : PB3 (TIMER0A) */
	NOT_ON_TIMER, /* D7 : PD2 */
	NOT_ON_TIMER, /* D8 : PD3 */
	TIMER1A,      /* D9 : PD5 (TIMER1A) */
	TIMER1B,      /* D10 : PD4 (TIMER1B) */
	TIMER2A,      /* D11 : PD7 (TIMER2A) */
	NOT_ON_TIMER, /* D12 : PC4 */
	NOT_ON_TIMER, /* D13 : PC6 */
	NOT_ON_TIMER, /* D14 : PA0 */
	NOT_ON_TIMER, /* D15 : PA1 */
	NOT_ON_TIMER, /* D16 : PA2 */
	NOT_ON_TIMER, /* D17 : PA3 */
	NOT_ON_TIMER, /* D18 : PA4 */
	NOT_ON_TIMER, /* D19 : PA5 */
	NOT_ON_TIMER, /* D20 : PA6 */
	NOT_ON_TIMER, /* D21 : PA7 */
	NOT_ON_TIMER, /* D22 : PC5 */
	NOT_ON_TIMER, /* D23 : PB0 */
	NOT_ON_TIMER, /* D24 : PB1 */
	NOT_ON_TIMER, /* D25 : PB2 */
	NOT_ON_TIMER, /* D26 : PB5 */
	NOT_ON_TIMER, /* D27 : PB6 */
	NOT_ON_TIMER, /* D28 : PB7 */
};
