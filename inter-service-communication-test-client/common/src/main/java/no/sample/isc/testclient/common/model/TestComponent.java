package no.sample.isc.testclient.common.model;

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
