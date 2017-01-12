package no.sample.isc.consumer.component;

import lombok.*;
import no.sample.isc.core.domain.GenericComponent;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TestComponent extends GenericComponent {
	private String val;
}
