package no.sample.isc.testclient.common;

import lombok.*;
import no.sample.isc.core.domain.GenericComponent;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TestComponent extends GenericComponent implements Serializable {
	private String val;
}
