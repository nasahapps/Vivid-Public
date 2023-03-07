//
//  FavoritesViewController.swift
//  
//
//  Created by Hakeem on 11/26/15.
//
//

import UIKit
import DZNEmptyDataSet

class FavoritesViewController: UIViewController {

    @IBOutlet weak var tableView: UITableView!
    
    var favoriteColors = [Color]()
    let colorManager = ColorManager.shared

    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
        tableView.emptyDataSetDelegate = self
        tableView.emptyDataSetSource = self
        tableView.delegate = self
        tableView.dataSource = self
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.hidesBarsOnSwipe = false
        
        self.getFavoriteColors()
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        if isIpad {
            let margin = self.view.frame.width / 6
            self.additionalSafeAreaInsets.left = margin
            self.additionalSafeAreaInsets.right = margin
            tableView.contentInset.top = margin / 2
            tableView.contentInset.bottom = margin / 2
        } else {
            self.additionalSafeAreaInsets.left = 0
            self.additionalSafeAreaInsets.right = 0
            tableView.contentInset.top = 0
            tableView.contentInset.bottom = 0
        }
    }

    func getFavoriteColors() {
        colorManager.getFavoriteColors { [weak self] (colors) in
            self?.favoriteColors = colors
            self?.tableView?.reloadData()
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        let cell = sender as! UITableViewCell
        let index = tableView.indexPath(for: cell)?.row ?? 0
        let vc = segue.destination as? ColorPageViewController
        var listOfFavoriteColors = [Color]()
        for color in self.favoriteColors {
            listOfFavoriteColors.append(color)
        }
        vc?.colors = listOfFavoriteColors
        vc?.currentPosition = index
    }
}

extension FavoritesViewController: DZNEmptyDataSetDelegate, DZNEmptyDataSetSource {
    func emptyDataSetShouldDisplay(_ scrollView: UIScrollView!) -> Bool {
        return self.favoriteColors.isEmpty
    }
    
    func title(forEmptyDataSet scrollView: UIScrollView!) -> NSAttributedString! {
        let text = "No Favorites"
        let attrs = [NSAttributedString.Key.font: UIFont.boldSystemFont(ofSize: 18), NSAttributedString.Key.foregroundColor: UIColor.darkGray]
        return NSAttributedString(string: text, attributes: attrs)
    }
    
    func description(forEmptyDataSet scrollView: UIScrollView!) -> NSAttributedString! {
        let text = "Star colors to add to your favorites list"
        let attrs = [NSAttributedString.Key.font: UIFont.systemFont(ofSize: 14), NSAttributedString.Key.foregroundColor: UIColor.lightGray]
        return NSAttributedString(string: text, attributes: attrs)
    }
}

extension FavoritesViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return favoriteColors.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "favorite", for: indexPath) as! FavoriteCell
        let color = favoriteColors[indexPath.row]
        cell.color.backgroundColor = ColorUtils.getColorForHexCode(hex: color.hexCode)
        cell.name.text = color.colorDescription
        cell.brand.text = color.brand
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
    }
    
    func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCell.EditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            let color = favoriteColors[indexPath.row]
            let alert = UIAlertController(title: "Remove \(color.colorDescription!) from favorites?", message: nil, preferredStyle: UIAlertController.Style.alert)
            let deleteAction = UIAlertAction(title: "Delete", style: UIAlertAction.Style.destructive, handler: { (action: UIAlertAction) -> Void in
                self.colorManager.setColor(color, asFavorite: false)
                self.favoriteColors.remove(at: indexPath.row)
                self.tableView.deleteRows(at: [indexPath], with: .automatic)
            })
            let cancelAction = UIAlertAction(title: "Cancel", style: UIAlertAction.Style.cancel, handler: nil)
            alert.addAction(deleteAction)
            alert.addAction(cancelAction)
            self.present(alert, animated: true, completion: nil)
        }
    }
}
