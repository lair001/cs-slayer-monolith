package com.cs_slayer.monolith;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CssmApplicationTest {

	@Test
	void testMain() {
		ConfigurableApplicationContext applicationContextMock = mock(ConfigurableApplicationContext.class);
		String[] args = {};

		try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
			springApplicationMock.when(() -> SpringApplication.run(CssmApplication.class, args)).thenReturn(applicationContextMock);
			CssmApplication.main(args);
			springApplicationMock.verify(() -> SpringApplication.run(CssmApplication.class, args), times(1));
		}
	}

}
