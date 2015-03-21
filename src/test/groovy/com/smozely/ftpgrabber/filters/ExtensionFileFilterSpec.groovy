package com.smozely.ftpgrabber.filters;

import spock.lang.Specification

class ExtensionFileFilterSpec extends Specification {

    def "Correctly Filters file names that contain the invalid extensions"(String name, boolean filtered) {
        given:
        ExtensionFileFilter underTest = new ExtensionFileFilter(".aaa", ".bbb", ".ccc")

        expect:
        underTest.test(name) == filtered

        where:

        name        | filtered
        "A.aaa"     | false
        "A.bbb"     | false
        "A.a.ccc"   | false
        "A.zzz"     | true
        "A.aaa.ddd" | true
        "aaa.B"     | true
    }

}