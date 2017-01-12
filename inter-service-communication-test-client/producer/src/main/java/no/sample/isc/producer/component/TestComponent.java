package no.sample.isc.producer.component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.sample.isc.core.domain.GenericComponent;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TestComponent extends GenericComponent {
	private String val;
}
