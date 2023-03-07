//
//  Brand+CoreDataProperties.swift
//  
//
//  Created by Hakeem Hasan on 8/9/19.
//
//

import Foundation
import CoreData


extension Brand {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<Brand> {
        return NSFetchRequest<Brand>(entityName: "Brand")
    }

    @NSManaged public var name: String?

}
