//
//  ColorManager2.swift
//  Vivid
//
//  Created by Hakeem Hasan on 8/9/19.
//  Copyright © 2019 Nasah Apps. All rights reserved.
//

import CoreData

class ColorManager {
    
    static let shared = ColorManager()
    
    private let colorsFilename = "realm_colors_db_v1.json"
    private let persistentContainer: NSPersistentContainer = {
        let container = NSPersistentContainer(name: "Vivid")
        container.loadPersistentStores(completionHandler: { (desc, error) in
            if let error = error {
                print("Error setting up CoreData", error)
            }
        })
        return container
    }()
    private let brandSortDescriptor = NSSortDescriptor(key: #keyPath(Brand.name), ascending: true)
    private let colorSortDescriptors = [
        NSSortDescriptor(key: #keyPath(Color.hue), ascending: true),
        NSSortDescriptor(key: #keyPath(Color.saturation), ascending: true),
        NSSortDescriptor(key: #keyPath(Color.value), ascending: true)
    ]
    
    private init() {}
    
    func loadDataIfNecessary(onProgress: @escaping (Int, Int) -> Void, onComplete: @escaping (Result<Any, Error>) -> Void) {
        let loadDataBlock = {
            self.persistentContainer.performBackgroundTask({ (context) in
                do {
                    if let filePath = Bundle.main.path(forResource: self.colorsFilename, ofType: nil) {
                        let fileUrl = URL(fileURLWithPath: filePath)
                        let data = try Data(contentsOf: fileUrl)
                        let json = try JSONSerialization.jsonObject(with: data, options: []) as! [String:Any]
                        
                        // Get the total number of colors we're parsing
                        var totalColors = 0
                        for key in json.keys {
                            let colors = json[key] as! [[String:Any]]
                            totalColors += colors.count
                        }
                        
                        // Parse through the brands and colors
                        var currentColor = 0
                        for key in json.keys {
                            let brand = Brand(context: context)
                            brand.name = key
                            
                            let colorsJson = json[key] as! [[String:Any]]
                            for colorJson in colorsJson {
                                let color = Color(context: context)
                                color.brand = key
                                color.colorDescription = colorJson["description"] as! String?
                                color.hexCode = colorJson["hexCode"] as! String?
                                color.hue = colorJson["sortHue"] as! Int16
                                color.saturation = colorJson["sortSaturation"] as! Int16
                                color.value = colorJson["sortValue"] as! Int16
                                
                                currentColor += 1
                                DispatchQueue.main.async {
                                    onProgress(currentColor, totalColors)
                                }
                            }
                        }
                        try context.save()
                        
                        DispatchQueue.main.async {
                            onComplete(.success(true))
                        }
                    }
                } catch {
                    print("Error parsing colors", error)
                    DispatchQueue.main.async {
                        onComplete(.failure(error))
                    }
                }
            })
        }
        
        getBrands { (brands) in
            if brands.isEmpty {
                loadDataBlock()
            } else {
                self.getColors(forBrand: brands.first!, onComplete: { (colors) in
                    if colors.isEmpty {
                        loadDataBlock()
                    } else {
                        print("Using saved colors")
                        onComplete(.success(true))
                    }
                })
            }
        }
    }
    
    func getBrands(onComplete: @escaping ([String]) -> Void) {
        let fetchRequest: NSFetchRequest<Brand> = Brand.fetchRequest()
        fetchRequest.sortDescriptors = [brandSortDescriptor]
        
        persistentContainer.viewContext.perform {
            do {
                let brands = try fetchRequest.execute()
                onComplete(brands.map { $0.name! })
            } catch {
                print("Error getting brands", error)
            }
        }
    }
    
    func getColors(forBrand brand: String, onComplete: @escaping ([Color]) -> Void) {
        let fetchRequest: NSFetchRequest<Color> = Color.fetchRequest()
        fetchRequest.sortDescriptors = colorSortDescriptors
        fetchRequest.predicate = NSPredicate(format: "%K == %@", #keyPath(Color.brand), brand)
        
        persistentContainer.viewContext.perform {
            do {
                onComplete(try fetchRequest.execute())
            } catch {
                print("Error getting colors for brand", error)
            }
        }
    }
    
    func getFavoriteColors(onComplete: @escaping ([Color]) -> Void) {
        let fetchRequest: NSFetchRequest<Color> = Color.fetchRequest()
        fetchRequest.sortDescriptors = colorSortDescriptors
        fetchRequest.predicate = NSPredicate(format: "%K == true", #keyPath(Color.isFavorite))
        
        persistentContainer.viewContext.perform {
            do {
                onComplete(try fetchRequest.execute())
            } catch {
                print("Error getting favorite colors", error)
            }
        }
    }
    
    func setColor(_ color: Color, asFavorite favorite: Bool) {
        color.isFavorite = favorite
        do {
            try persistentContainer.viewContext.save()
        } catch {
            print("Error setting color as favorite to \(favorite)", error)
        }
    }
    
    func findColors(byQuery query: String, onComplete: @escaping ([Color]) -> Void) {
        if query.isEmpty {
            onComplete([])
        } else {
            let fetchRequest: NSFetchRequest<Color> = Color.fetchRequest()
            fetchRequest.sortDescriptors = colorSortDescriptors
            let keyPath: CVarArg
            if query.starts(with: "#") {
                keyPath = #keyPath(Color.hexCode)
            } else {
                keyPath = #keyPath(Color.colorDescription)
            }
            fetchRequest.predicate = NSPredicate(format: "%K CONTAINS[cd] %@", keyPath, query)
            
            persistentContainer.viewContext.perform {
                do {
                    onComplete(try fetchRequest.execute())
                } catch {
                    print("Error finding colors for \(query)", error)
                }
            }
        }
    }
    
}
