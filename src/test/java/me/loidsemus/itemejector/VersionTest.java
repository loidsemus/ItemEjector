package me.loidsemus.itemejector;

import me.loidsemus.itemejector.utils.SemanticVersion;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VersionTest {

    @Test
    public void compareTo_withEarlierVersion_isGreaterThan() {
        assertThat(new SemanticVersion("2.0.0").compareTo(new SemanticVersion("1.0.0"))).isEqualTo(1);
    }

    @Test
    public void compareTo_withSameVersion_isEqual() {
        assertThat(new SemanticVersion("2.0.0").compareTo(new SemanticVersion("2.0.0"))).isEqualTo(0);
    }

    @Test
    public void compareTo_withLaterVersion_isLessThan() {
        assertThat(new SemanticVersion("1.0.0").compareTo(new SemanticVersion("2.0.0"))).isEqualTo(-1);
    }

    @Test
    public void compareTo_withMorePreciseSameVersion_isFalse() {
        assertThat(new SemanticVersion("1").compareTo(new SemanticVersion("1.0.0"))).isEqualTo(0);
    }

    @Test
    public void compareTo_withMorePreciseEarlierVersion_isFalse() {
        assertThat(new SemanticVersion("2").compareTo(new SemanticVersion("1.0.0"))).isEqualTo(1);
    }

    @Test
    public void compareTo_withMorePreciseLaterVersion_isLessThan() {
        assertThat(new SemanticVersion("1").compareTo(new SemanticVersion("1.0.1"))).isEqualTo(-1);
    }
}
