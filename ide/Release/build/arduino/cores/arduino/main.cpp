#include <WProgram.h>

#define	kDelayValue	0x7fff
int main(void)
{

	init();

	setup();
    
	for (;;)
		loop();
        
	return 0;
}

