//
//  VividTests.swift
//  VividTests
//
//  Created by Hakeem on 8/17/15.
//  Copyright (c) 2015 Nasah Apps. All rights reserved.
//

import UIKit
import XCTest

class VividTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
    func testUrlFormatting() {
        let input = "Behr Scented Valentine"
        let expectedResult = "Behr%20Scented%20Valentine"
        XCTAssertEqual(input.addingPercentEncoding(withAllowedCharacters: .urlPathAllowed), expectedResult)
    }
}
