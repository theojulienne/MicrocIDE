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
//************************************************************************
//*	Jun  1,	2010	<MLS> Mark Sproul adding support for wiring board 1281
//*	Jun  4,	2010	Got my Wiring_1281 board, Arduino working with stk500v2
//*	Jul  5,	2010	<MLS> Lots of work getting the PWMs on the right pins for wiring board 1281
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
#endif

//*****************************************************************
//*	Jun  1,	2010	Mark Sproul adding support for wiring board
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
	NOT_A_PORT,	//	&PORTH,
	NOT_A_PORT,
	NOT_A_PORT,	//	&PORTJ,
	NOT_A_PORT,	//	&PORTK,
	NOT_A_PORT,	//	&PORTL,
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
	NOT_A_PIN,	//	&PINH,
	NOT_A_PIN,
	NOT_A_PIN,	//	&PINJ,
	NOT_A_PIN,	//	&PINK,
	NOT_A_PIN,	//	&PINL,
};
//*	_BOARD_WIRING_1281_
const uint8_t PROGMEM digital_pin_to_port_PGM[] = {
	// PORTLIST		
	// -------------------------------------------		
	PD	, // PD 0 ** 0 ** 
	PD	, // PD 1 ** 1 ** 
	PD	, // PD 2 ** 2 ** 
	PD	, // PD 3 ** 3 ** 
	PD	, // PD 4 ** 4 ** 
	PD	, // PD 5 ** 5 ** 
	PD	, // PD 6 ** 6 ** 
	PD	, // PD 7 ** 7 ** 

	PC	, // PC 0 ** 8 ** 
	PC	, // PC 1 ** 9 ** 
	PC	, // PC 2 ** 10 **
	PC	, // PC 3 ** 11 **
	PC	, // PC 4 ** 12 **
	PC	, // PC 5 ** 13 **
	PC	, // PC 6 ** 14 **
	PC	, // PJ 7 ** 15 **

	PA	, // PA 0 ** 16 ** 
	PA	, // PA 1 ** 17 ** 
	PA	, // PA 2 ** 18 ** 
	PA	, // PA 3 ** 19 ** 
	PA	, // PA 4 ** 20 ** 
	PA	, // PA 5 ** 21 ** 
	PA	, // PA 6 ** 22 ** 
	PA	, // PA 7 ** 23 ** 

	PB	, // PB 0 ** 24 ** 
	PB	, // PB 1 ** 25 ** 
	PB	, // PB 2 ** 26 ** 
	PB	, // PB 3 ** 27 ** 
	PB	, // PB 4 ** 28 ** 
	PB	, // PB 5 ** 29 ** 
	PB	, // PB 6 ** 30 ** 
	PB	, // PB 7 ** 31 ** 

	PE	, // PE 0 ** 32 ** 
	PE	, // PE 1 ** 33 ** 
	PE	, // PE 2 ** 34 ** 
	PE	, // PE 3 ** 35 ** 
	PE	, // PE 4 ** 36 ** 
	PE	, // PE 5 ** 37 ** 
	PE	, // PE 6 ** 38 ** 
	PE	, // PE 7 ** 39 ** 

	PF	, // PF 0 ** 40 ** A0
	PF	, // PF 1 ** 41 ** A1
	PF	, // PF 2 ** 42 ** A2
	PF	, // PF 3 ** 43 ** A3
	PF	, // PF 4 ** 44 ** A4
	PF	, // PF 5 ** 45 ** A5
	PF	, // PF 6 ** 46 ** A6
	PF	, // PF 7 ** 47 ** A7

	PG	, // PG 0 ** 48 ** 
	PG	, // PG 1 ** 49 ** 
	PG	, // PG 2 ** 50 ** 
	PG	, // PG 3 ** 51 ** 
	PG	, // PG 4 ** 52 ** 
	PG	, // PG 5 ** 53 ** 

};

//*	_BOARD_WIRING_1281_
const uint8_t PROGMEM digital_pin_to_bit_mask_PGM[] = {
	// -------------------------------------------		
	// PIN IN PORT		
	_BV( 0 )	, // PD 0 ** 0 ** 
	_BV( 1 )	, // PD 1 ** 1 ** 
	_BV( 2 )	, // PD 2 ** 2 ** 
	_BV( 3 )	, // PD 3 ** 3 ** 
	_BV( 4 )	, // PD 4 ** 4 ** 
	_BV( 5 )	, // PD 5 ** 5 ** 
	_BV( 6 )	, // PD 6 ** 6 ** 
	_BV( 7 )	, // PD 7 ** 7 ** 

	_BV( 0 )	, // PC 0 ** 8 ** 
	_BV( 1 )	, // PC 1 ** 9 ** 
	_BV( 2 )	, // PC 2 ** 10 **
	_BV( 3 )	, // PC 3 ** 11 **
	_BV( 4 )	, // PC 4 ** 12 **
	_BV( 5 )	, // PC 5 ** 13 **
	_BV( 6 )	, // PC 6 ** 14 **
	_BV( 7 )	, // PJ 7 ** 15 **

	_BV( 0 )	, // PA 0 ** 16 ** 
	_BV( 1 )	, // PA 1 ** 17 ** 
	_BV( 2 )	, // PA 2 ** 18 ** 
	_BV( 3 )	, // PA 3 ** 19 ** 
	_BV( 4 )	, // PA 4 ** 20 ** 
	_BV( 5 )	, // PA 5 ** 21 ** 
	_BV( 6 )	, // PA 6 ** 22 ** 
	_BV( 7 )	, // PA 7 ** 23 ** 

	_BV( 0 )	, // PB 0 ** 24 ** 
	_BV( 1 )	, // PB 1 ** 25 ** 
	_BV( 2 )	, // PB 2 ** 26 ** 
	_BV( 3 )	, // PB 3 ** 27 ** 
	_BV( 4 )	, // PB 4 ** 28 ** 
	_BV( 5 )	, // PB 5 ** 29 ** 
	_BV( 6 )	, // PB 6 ** 30 ** 
	_BV( 7 )	, // PB 7 ** 31 ** 

	_BV( 0 )	, // PE 0 ** 32 ** 
	_BV( 1 )	, // PE 1 ** 33 ** 
	_BV( 2 )	, // PE 2 ** 34 ** 
	_BV( 3 )	, // PE 3 ** 35 ** 
	_BV( 4 )	, // PE 4 ** 36 ** 
	_BV( 5 )	, // PE 5 ** 37 ** 
	_BV( 6 )	, // PE 6 ** 38 ** 
	_BV( 7 )	, // PE 7 ** 39 ** 

	_BV( 0 )	, // PF 0 ** 40 ** A0
	_BV( 1 )	, // PF 1 ** 41 ** A1
	_BV( 2 )	, // PF 2 ** 42 ** A2
	_BV( 3 )	, // PF 3 ** 43 ** A3
	_BV( 4 )	, // PF 4 ** 44 ** A4
	_BV( 5 )	, // PF 5 ** 45 ** A5
	_BV( 6 )	, // PF 6 ** 46 ** A6
	_BV( 7 )	, // PF 7 ** 47 ** A7

	_BV( 0 )	, // PG 0 ** 48 ** 
	_BV( 1 )	, // PG 1 ** 49 ** 
	_BV( 2 )	, // PG 2 ** 50 ** 
	_BV( 3 )	, // PG 3 ** 51 ** 
	_BV( 4 )	, // PG 4 ** 52 ** 
	_BV( 5 )	, // PG 5 ** 53 ** 


};

//*	_BOARD_WIRING_1281_
const uint8_t PROGMEM digital_pin_to_timer_PGM[] = {
//*	Note!! I copied the order of the timers from the 1280 (megq) and assigned
//*	to the pins that the wiring documentation said had PWM
	// TIMERS		
	// -------------------------------------------		
	NOT_ON_TIMER	, // PD 0 ** 0 ** 
	NOT_ON_TIMER	, // PD 1 ** 1 ** 
	NOT_ON_TIMER	, // PD 2 ** 2 ** 
	NOT_ON_TIMER	, // PD 3 ** 3 ** 
	NOT_ON_TIMER	, // PD 4 ** 4 ** 
	NOT_ON_TIMER	, // PD 5 ** 5 ** 
	NOT_ON_TIMER	, // PD 6 ** 6 ** 
	NOT_ON_TIMER	, // PD 7 ** 7 ** 

	NOT_ON_TIMER	, // PC 0 ** 8 ** 
	NOT_ON_TIMER	, // PC 1 ** 9 ** 
	NOT_ON_TIMER	, // PC 2 ** 10 **
	NOT_ON_TIMER	, // PC 3 ** 11 **
	NOT_ON_TIMER	, // PC 4 ** 12 **
	NOT_ON_TIMER	, // PC 5 ** 13 **
	NOT_ON_TIMER	, // PC 6 ** 14 **
	NOT_ON_TIMER	, // PJ 7 ** 15 **

	NOT_ON_TIMER	, // PA 0 ** 16 ** 
	NOT_ON_TIMER	, // PA 1 ** 17 ** 
	NOT_ON_TIMER	, // PA 2 ** 18 ** 
	NOT_ON_TIMER	, // PA 3 ** 19 ** 
	NOT_ON_TIMER	, // PA 4 ** 20 ** 
	NOT_ON_TIMER	, // PA 5 ** 21 ** 
	NOT_ON_TIMER	, // PA 6 ** 22 ** 
	NOT_ON_TIMER	, // PA 7 ** 23 ** 

	NOT_ON_TIMER	, // PB 0 ** 24 ** 
	NOT_ON_TIMER	, // PB 1 ** 25 ** 
	NOT_ON_TIMER	, // PB 2 ** 26 ** 
	NOT_ON_TIMER	, // PB 3 ** 27 ** 
	TIMER2A			, // PB 4 ** 28 ** 
	TIMER1A			, // PB 5 ** 29 ** 
	TIMER1B			, // PB 6 ** 30 ** 
	TIMER0A			, // PB 7 ** 31 ** 

	NOT_ON_TIMER	, // PE 0 ** 32 ** 
	NOT_ON_TIMER	, // PE 1 ** 33 ** 
	NOT_ON_TIMER	, // PE 2 ** 34 ** 
	TIMER3A			, // PE 3 ** 35 ** 
	TIMER3B			, // PE 4 ** 36 ** 
	TIMER3C			, // PE 5 ** 37 ** 
	NOT_ON_TIMER	, // PE 6 ** 38 ** 
	NOT_ON_TIMER	, // PE 7 ** 39 ** 

	NOT_ON_TIMER	, // PF 0 ** 40 ** A0
	NOT_ON_TIMER	, // PF 1 ** 41 ** A1
	NOT_ON_TIMER	, // PF 2 ** 42 ** A2
	NOT_ON_TIMER	, // PF 3 ** 43 ** A3
	NOT_ON_TIMER	, // PF 4 ** 44 ** A4
	NOT_ON_TIMER	, // PF 5 ** 45 ** A5
	NOT_ON_TIMER	, // PF 6 ** 46 ** A6
	NOT_ON_TIMER	, // PF 7 ** 47 ** A7

	NOT_ON_TIMER	, // PG 0 ** 48 ** 
	NOT_ON_TIMER	, // PG 1 ** 49 ** 
	NOT_ON_TIMER	, // PG 2 ** 50 ** 
	NOT_ON_TIMER	, // PG 3 ** 51 ** 
	NOT_ON_TIMER	, // PG 4 ** 52 ** 
	TIMER0B			, // PG 5 ** 53 ** 
};
