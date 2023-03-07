//
//  Color+CoreDataProperties.swift
//  
//
//  Created by Hakeem Hasan on 8/9/19.
//
//

import Foundation
import CoreData


extension Color {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<Color> {
        return NSFetchRequest<Color>(entityName: "Color")
    }

    @NSManaged public var hexCode: String?
    @NSManaged public var colorDescription: String?
    @NSManaged public var hue: Int16
    @NSManaged public var saturation: Int16
    @NSManaged public var value: Int16
    @NSManaged public var isFavorite: Bool
    @NSManaged public var brand: String?

}
