/*
  HardwareSerial.cpp - Hardware serial library for Wiring
  Copyright (c) 2006 Nicholas Zambetti.  All right reserved.

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
  
  Modified 23 November 2006 by David A. Mellis


*/
//************************************************************************
//*	Mar 14, 2010	<MLS> Mark Sproul fixed bug in __AVR_ATmega644__ interrupt vector
//*	Mar 25, 2010	<MLS> Fixed bug in __AVR_ATmega644P__ interrupt vector
//*	Jul 30, 2010	<MLS> Chainging #ifdefs to register and signals instead of CPU type
//*	Aug  3,	2010	<MLS> Tested on 644P 645 1280, 2560 328�and others
//*	Aug 16,	2010	<MLS> Added support for Atmega32
//************************************************************************

#include <stdio.h>
#include <string.h>
#include <inttypes.h>
#include "wiring.h"
#include "wiring_private.h"

#include "HardwareSerial.h"

// Define constants and variables for buffering incoming serial data.  We're
// using a ring buffer (I think), in which rx_buffer_head is the index of the
// location to which to write the next incoming character and rx_buffer_tail
// is the index of the location from which to read.
#if (RAMEND < 1500)
	#define RX_BUFFER_SIZE 32
#else
	#define RX_BUFFER_SIZE 128
#endif

struct ring_buffer {
  unsigned char buffer[RX_BUFFER_SIZE];
  int head;
  int tail;
};

ring_buffer rx_buffer	=	{ { 0 }, 0, 0 };


//#if defined(__AVR_ATmega1280__) || defined(__AVR_ATmega1281__) || defined(__AVR_ATmega2560__) || defined(__AVR_ATmega128__) || defined(__AVR_ATmega644P__)
#if defined(UBRR1H)
	ring_buffer rx_buffer1	=	{ { 0 }, 0, 0 };
#endif

//#if defined(__AVR_ATmega1280__) || defined(__AVR_ATmega2560__)
#if defined(UBRR2H)
	ring_buffer rx_buffer2	=	{ { 0 }, 0, 0 };
#endif

#if defined(UBRR3H)
	ring_buffer rx_buffer3	=	{ { 0 }, 0, 0 };
#endif


//************************************************************************
inline void store_char(unsigned char c, ring_buffer *rx_buffer)
{
	int i	=	(rx_buffer->head + 1) % RX_BUFFER_SIZE;

	// if we should be storing the received character into the location
	// just before the tail (meaning that the head would advance to the
	// current location of the tail), we're about to overflow the buffer
	// and so we don't write the character or advance the head.
	if (i != rx_buffer->tail)
	{
		rx_buffer->buffer[rx_buffer->head]	=	c;
		rx_buffer->head	=	i;
	}
}

//************************************************************************
//#if defined(__AVR_ATmega1280__) || defined(__AVR_ATmega1281__) || defined(__AVR_ATmega2560__) || defined(__AVR_ATmega128__)

#if defined(USART_RX_vect)
SIGNAL(USART_RX_vect)
{
	unsigned char c	=	UDR0;
	store_char(c, &rx_buffer);
}
#elif defined(SIG_USART0_RECV)
//************************************************************************
SIGNAL(SIG_USART0_RECV)
{
	unsigned char c	=	UDR0;
	store_char(c, &rx_buffer);
}
#elif defined(SIG_USART_RECV)
//************************************************************************
//*	fixed by Mark Sproul March 25, 2010
//*	this is on the 644/644p
SIGNAL(SIG_USART_RECV)
{
#if defined(UDR0)
	unsigned char c	=	UDR0;
#elif defined(UDR)
	unsigned char c	=	UDR;	//	atmega8, atmega32
#else
	#error UDR not defined
#endif
	store_char(c, &rx_buffer);
}
#elif defined(SIG_UART_RECV)
//************************************************************************
//*	this is for atmega8
SIGNAL(SIG_UART_RECV)
{
#if defined(UDR0)
	unsigned char c	=	UDR0;	//	atmega645
#elif defined(UDR)
	unsigned char c	=	UDR;	//	atmega8
#endif

	store_char(c, &rx_buffer);
}
#else
	#error No interrupt handler for usart 0
#endif

#if defined(SIG_USART1_RECV)
//************************************************************************
SIGNAL(SIG_USART1_RECV)
{
	unsigned char c = UDR1;
	store_char(c, &rx_buffer1);
}
#endif


//#if defined(__AVR_ATmega1280__) || defined(__AVR_ATmega2560__)
#if defined(SIG_USART2_RECV)
//************************************************************************
SIGNAL(SIG_USART2_RECV)
{
	unsigned char c = UDR2;
	store_char(c, &rx_buffer2);
}
#endif

#if defined(SIG_USART3_RECV)
//************************************************************************
SIGNAL(SIG_USART3_RECV)
{
	unsigned char c = UDR3;
	store_char(c, &rx_buffer3);
}
#endif


// Constructors ////////////////////////////////////////////////////////////////

//************************************************************************
HardwareSerial::HardwareSerial(ring_buffer *rx_buffer,
  volatile uint8_t *ubrrh, volatile uint8_t *ubrrl,
  volatile uint8_t *ucsra, volatile uint8_t *ucsrb,
  volatile uint8_t *udr,
  uint8_t rxen, uint8_t txen, uint8_t rxcie, uint8_t udre, uint8_t u2x)
{
	_rx_buffer	=	rx_buffer;
	_ubrrh		=	ubrrh;
	_ubrrl		=	ubrrl;
	_ucsra		=	ucsra;
	_ucsrb		=	ucsrb;
	_udr		=	udr;
	_rxen		=	rxen;
	_txen		=	txen;
	_rxcie		=	rxcie;
	_udre		=	udre;
	_u2x		=	u2x;
}

// Public Methods //////////////////////////////////////////////////////////////

//************************************************************************
void HardwareSerial::begin(long baud)
{
	uint16_t baud_setting;
	bool use_u2x;

	// U2X mode is needed for baud rates higher than (CPU Hz / 16)
	if (baud > F_CPU / 16)
	{
		use_u2x	=	true;
	}
	else
	{
		// figure out if U2X mode would allow for a better connection

		// calculate the percent difference between the baud-rate specified and
		// the real baud rate for both U2X and non-U2X mode (0-255 error percent)
		uint8_t nonu2x_baud_error	=	abs((int)(255-((F_CPU/(16*(((F_CPU/8/baud-1)/2)+1))*255)/baud)));
		uint8_t u2x_baud_error		=	abs((int)(255-((F_CPU/(8*(((F_CPU/4/baud-1)/2)+1))*255)/baud)));

		// prefer non-U2X mode because it handles clock skew better
		use_u2x	=	(nonu2x_baud_error > u2x_baud_error);
	}

	if (use_u2x)
	{
		*_ucsra	=	1 << _u2x;
		baud_setting	=	(F_CPU / 4 / baud - 1) / 2;
	}
	else
	{
		*_ucsra	=	0;
		baud_setting	=	(F_CPU / 8 / baud - 1) / 2;
	}

	// assign the baud_setting, a.k.a. ubbr (USART Baud Rate Register)
	*_ubrrh	=	baud_setting >> 8;
	*_ubrrl	=	baud_setting;

	sbi(*_ucsrb, _rxen);
	sbi(*_ucsrb, _txen);
	sbi(*_ucsrb, _rxcie);
}

//************************************************************************
void HardwareSerial::end()
{
	cbi(*_ucsrb, _rxen);
	cbi(*_ucsrb, _txen);
	cbi(*_ucsrb, _rxcie);  
}

//************************************************************************
uint8_t HardwareSerial::available(void)
{
	return (RX_BUFFER_SIZE + _rx_buffer->head - _rx_buffer->tail) % RX_BUFFER_SIZE;
}

//************************************************************************
int HardwareSerial::read(void)
{
	// if the head isn't ahead of the tail, we don't have any characters
	if (_rx_buffer->head == _rx_buffer->tail)
	{
		return -1;
	}
	else
	{
		unsigned char c		=	_rx_buffer->buffer[_rx_buffer->tail];
		_rx_buffer->tail	=	(_rx_buffer->tail + 1) % RX_BUFFER_SIZE;
		return c;
	}
}

//************************************************************************
void HardwareSerial::flush()
{
	// don't reverse this or there may be problems if the RX interrupt
	// occurs after reading the value of rx_buffer_head but before writing
	// the value to rx_buffer_tail; the previous value of rx_buffer_head
	// may be written to rx_buffer_tail, making it appear as if the buffer
	// don't reverse this or there may be problems if the RX interrupt
	// occurs after reading the value of rx_buffer_head but before writing
	// the value to rx_buffer_tail; the previous value of rx_buffer_head
	// may be written to rx_buffer_tail, making it appear as if the buffer
	// were full, not empty.
	_rx_buffer->head	=	_rx_buffer->tail;
}

//************************************************************************
void HardwareSerial::write(uint8_t c)
{
	while (!((*_ucsra) & (1 << _udre)))
		;

	*_udr	=	c;
}

// Preinstantiate Objects //////////////////////////////////////////////////////

//#if defined(__AVR_ATmega8__)
#if defined(UBRRH)
	HardwareSerial Serial(&rx_buffer, &UBRRH, &UBRRL, &UCSRA, &UCSRB, &UDR, RXEN, TXEN, RXCIE, UDRE, U2X);
#elif defined(UBRR0H)
	HardwareSerial Serial(&rx_buffer, &UBRR0H, &UBRR0L, &UCSR0A, &UCSR0B, &UDR0, RXEN0, TXEN0, RXCIE0, UDRE0, U2X0);
#else
	#error no serial port defined
#endif

//#if defined(__AVR_ATmega644P__) || defined(__AVR_ATmega1280__) || defined(__AVR_ATmega1281__) || defined(__AVR_ATmega2560__) || defined(__AVR_ATmega128__)
#if defined(UBRR1H)
	HardwareSerial Serial1(&rx_buffer1, &UBRR1H, &UBRR1L, &UCSR1A, &UCSR1B, &UDR1, RXEN1, TXEN1, RXCIE1, UDRE1, U2X1);
#endif

//#if defined(__AVR_ATmega1280__) || defined(__AVR_ATmega2560__)
#if defined(UBRR2H)
	HardwareSerial Serial2(&rx_buffer2, &UBRR2H, &UBRR2L, &UCSR2A, &UCSR2B, &UDR2, RXEN2, TXEN2, RXCIE2, UDRE2, U2X2);
#endif
#if defined(UBRR3H)
	HardwareSerial Serial3(&rx_buffer3, &UBRR3H, &UBRR3L, &UCSR3A, &UCSR3B, &UDR3, RXEN3, TXEN3, RXCIE3, UDRE3, U2X3);
#endif

