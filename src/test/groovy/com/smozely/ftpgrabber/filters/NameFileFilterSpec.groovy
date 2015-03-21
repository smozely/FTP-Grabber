package com.smozely.ftpgrabber.filters;

import spock.lang.Specification

class NameFileFilterSpec extends Specification {

    def "Correctly Filters File Names containing one of the provided strings"(String name, boolean filtered) {
        given:
        NameFileFilter underTest = new NameFileFilter("AAA", "BBB", "ABC")

        expect:
        underTest.test(name) == filtered

        where:

        name    | filtered
        "AAA"   | false
        "AAAZZ" | false
        "ZZBBB" | false
        "ZABCZ" | false
        "CCC"   | true
        "A"     | true
        "B"     | true
        "AB"    | true
        "BC"    | true

    }

}