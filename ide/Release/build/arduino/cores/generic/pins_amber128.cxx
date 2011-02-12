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

  $Id: pins_arduino.c 804 2009-12-18 16:05:52Z dmellis $
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

// On the Arduino board, digital pins are also used
// for the analog output (software PWM).  Analog input
// pins are a separate set.

// ATMEL ATMEGA8 & 168 / ARDUINO
//
//                  +-\/-+
//            PC6  1|    |28  PC5 (AI 5)
//      (D 0) PD0  2|    |27  PC4 (AI 4)
//      (D 1) PD1  3|    |26  PC3 (AI 3)
//      (D 2) PD2  4|    |25  PC2 (AI 2)
// PWM+ (D 3) PD3  5|    |24  PC1 (AI 1)
//      (D 4) PD4  6|    |23  PC0 (AI 0)
//            VCC  7|    |22  GND
//            GND  8|    |21  AREF
//            PB6  9|    |20  AVCC
//            PB7 10|    |19  PB5 (D 13)
// PWM+ (D 5) PD5 11|    |18  PB4 (D 12)
// PWM+ (D 6) PD6 12|    |17  PB3 (D 11) PWM
//      (D 7) PD7 13|    |16  PB2 (D 10) PWM
//      (D 8) PB0 14|    |15  PB1 (D 9) PWM
//                  +----+
//
// (PWM+ indicates the additional PWM pins on the ATmega168.)

// ATMEL ATMEGA1280 / ARDUINO
//
// 0-7 PE0-PE7   works
// 8-13 PB0-PB5  works
// 14-21 PA0-PA7 works 
// 22-29 PH0-PH7 works
// 30-35 PG5-PG0 works
// 36-43 PC7-PC0 works
// 44-51 PJ7-PJ0 works
// 52-59 PL7-PL0 works
// 60-67 PD7-PD0 works
// A0-A7 PF0-PF7
// A8-A15 PK0-PK7

#ifndef PA
	#define PA 1
	#define PB 2
	#define PC 3
	#define PD 4
	#define PE 5
	#define PF 6
	#define PG 7
	#define PH 8
	#define PJ 10
	#define PK 11
	#define PL 12
#endif


// these arrays map port names (e.g. port B) to the
// appropriate addresses for various functions (e.g. reading
// and writing)

//__AVR_ATmega128__
const uint16_t PROGMEM port_to_mode_PGM[] = {
	NOT_A_PORT,
	&DDRA,
	&DDRB,
	&DDRC,
	&DDRD,
	&DDRE,
	&DDRF,
	&DDRG,
	NOT_A_PORT,
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

const uint8_t PROGMEM digital_pin_to_port_PGM[] = {
	// PORTLIST		
	//-------------------------------------------		
	//	ALE	 ** Physical pin 1 ** 
	//	NC	 ** Physical pin 2 ** 
	PD,			//	PD 7 ** logical pin 0 Physical pin 3 ** /T2
	PD,			//	PD 6 ** logical pin 1 Physical pin 4 ** T1
	PD,			//	PD 5 ** logical pin 2 Physical pin 5 ** XCK1
	PD,			//	PD 4 ** logical pin 3 Physical pin 6 ** ICP1
	PD,			//	PD 3 ** logical pin 4 Physical pin 7 ** TXD1
	PD,			//	PD 2 ** logical pin 5 Physical pin 8 ** RXD1
	PD,			//	PD 1 ** logical pin 6 Physical pin 9 ** SDA
	PD,			//	PD 0 ** logical pin 7 Physical pin 10 ** SCL

	PB,			//	PB 7 ** logical pin 8 Physical pin 11 ** 
	PB,			//	PB 6 ** logical pin 9 Physical pin 12 ** 
	PB,			//	PB 5 ** logical pin 10 Physical pin 13 ** 
	PB,			//	PB 4 ** logical pin 11 Physical pin 14 ** 
	PB,			//	PB 3 ** logical pin 12 Physical pin 15 ** 
	PB,			//	PB 2 ** logical pin 13 Physical pin 16 ** 
	PB,			//	PB 1 ** logical pin 14 Physical pin 17 ** 
	PB,			//	PB 1 ** logical pin 15 Physical pin 18 ** 

	PE,			//	PE 7 ** logical pin 16 Physical pin 19 ** 
	PE,			//	PE 6 ** logical pin 17 Physical pin 20 ** 
	PE,			//	PE 5 ** logical pin 18 Physical pin 21 ** 
	PE,			//	PE 4 ** logical pin 19 Physical pin 22 ** 
	PE,			//	PE 3 ** logical pin 20 Physical pin 23 ** 
	PE,			//	PE 2 ** logical pin 21 Physical pin 24 ** 
	PE,			//	PE 1 ** logical pin 22 Physical pin 25 ** 
	PE,			//	PE 0 ** logical pin 23 Physical pin 26 ** 

	PF,			//	PF 0 ** logical pin 24 ** ANALOG 1
	PF,			//	PF 1 ** logical pin 25 ** ANALOG 2
	PF,			//	PF 2 ** logical pin 26 ** ANALOG 3
	PF,			//	PF 3 ** logical pin 27 ** ANALOG 4
	PF,			//	PF 4 ** logical pin 28 ** ANALOG 5
	PF,			//	PF 5 ** logical pin 29 ** ANALOG 6
	PF,			//	PF 6 ** logical pin 30 ** ANALOG 7
	PF,			//	PF 7 ** logical pin 31 ** ANALOG 8

	
};
//_BOARD_AMBER128_
const uint8_t PROGMEM digital_pin_to_bit_mask_PGM[] = {
	// PIN IN PORT		
	// -------------------------------------------		
	_BV( 7 )	, // PD 
	_BV( 6 )	, // PD 
	_BV( 5 )	, // PD 
	_BV( 4 )	, // PD 
	_BV( 3 )	, // PD 
	_BV( 2 )	, // PD 
	_BV( 1 )	, // PD 
	_BV( 0 )	, // PD 

	_BV( 7 )	, // PD 
	_BV( 6 )	, // PD 
	_BV( 5 )	, // PD 
	_BV( 4 )	, // PD 
	_BV( 3 )	, // PD 
	_BV( 2 )	, // PD 
	_BV( 1 )	, // PD 
	_BV( 0 )	, // PD 

	_BV( 7 )	, // PD 
	_BV( 6 )	, // PD 
	_BV( 5 )	, // PD 
	_BV( 4 )	, // PD 
	_BV( 3 )	, // PD 
	_BV( 2 )	, // PD 
	_BV( 1 )	, // PD 
	_BV( 0 )	, // PD 

	_BV( 0 )	, // PF 
	_BV( 1 )	, // PF 
	_BV( 2 )	, // PF 
	_BV( 3 )	, // PF 
	_BV( 4 )	, // PF 
	_BV( 5 )	, // PF 
	_BV( 6 )	, // PF 
	_BV( 7 )	, // PF
};

const uint8_t PROGMEM digital_pin_to_timer_PGM[] = {
	// TIMERS		
	// -------------------------------------------		
	NOT_ON_TIMER,		//	PD 7 ** 0 ** /T2
	NOT_ON_TIMER	,	//	PD 6 ** 1 ** T1
	NOT_ON_TIMER	,	//	PD 5 ** 2 ** XCK1
	NOT_ON_TIMER	,	//	PD 4 ** 3 ** ICP1
	NOT_ON_TIMER	,	//	PD 3 ** 4 ** TXD1
	NOT_ON_TIMER	,	//	PD 2 ** 5 ** RXD1
	NOT_ON_TIMER	,	//	PD 1 ** 6 ** SDA
	NOT_ON_TIMER	,	//	PD 0 ** 7 ** SCL

	TIMER2			,	//	PB 7 ** 8 ** 		OK
	TIMER1B			,	//	PB 6 ** 9 ** 		OK
	TIMER1A			,	//	PB 5 ** 10 ** 		OK
	TIMER0A			,	//	PB 4 ** 11 ** 		not working
	NOT_ON_TIMER	,	//	PB 3 ** 12 ** 
	NOT_ON_TIMER	,	//	PB 2 ** 13 ** 
	NOT_ON_TIMER	,	//	PB 1 ** 14 ** 
	NOT_ON_TIMER	,	//	PB 1 ** 15 ** 

	NOT_ON_TIMER	,	//	PE 7 ** 16 ** 
	NOT_ON_TIMER	,	//	PE 6 ** 17 ** 
	TIMER3C			,	//	PE 5 ** 18 ** 		OK
	TIMER3B			,	//	PE 4 ** 19 ** 		OK
	TIMER3A			,	//	PE 3 ** 20 ** 		OK
	NOT_ON_TIMER	,	//	PE 2 ** 21 ** 
	NOT_ON_TIMER	,	//	PE 1 ** 22 ** 
	NOT_ON_TIMER	,	//	PE 0 ** 23 ** 

	NOT_ON_TIMER	,	//	PF 0 ** 24 ** ANALOG 0
	NOT_ON_TIMER	,	//	PF 1 ** 25 ** ANALOG 1
	NOT_ON_TIMER	,	//	PF 2 ** 26 ** ANALOG 2
	NOT_ON_TIMER	,	//	PF 3 ** 27 ** ANALOG 3
	NOT_ON_TIMER	,	//	PF 4 ** 28 ** ANALOG 4
	NOT_ON_TIMER	,	//	PF 5 ** 29 ** ANALOG 5
	NOT_ON_TIMER	,	//	PF 6 ** 30 ** ANALOG 6
	NOT_ON_TIMER	,	//	PF 7 ** 31 ** ANALOG 7
};

