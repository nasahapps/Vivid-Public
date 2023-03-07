//
//  SearchTableViewController.swift
//  Vivid
//
//  Created by Hakeem on 11/29/15.
//  Copyright © 2015 Nasah Apps. All rights reserved.
//

import UIKit
import DZNEmptyDataSet

class SearchTableViewController: UIViewController {

    var searchBar = UISearchBar()
    @IBOutlet weak var tableView: UITableView!

    var colors = [Color]()
    let colorManager = ColorManager.shared

    override func viewDidLoad() {
        super.viewDidLoad()

        searchBar.sizeToFit()
        searchBar.placeholder = "\"Ruby Red\" or \"#ebcc39\""
        let searchBarParent = SearchBarContainerView(searchBar: searchBar)
        searchBarParent.frame = CGRect(x: 0, y: 0, width: view.frame.width, height: 44)
        navigationItem.titleView = searchBarParent
        searchBar.delegate = self
        searchBar.becomeFirstResponder()
        searchBar.searchBarStyle = .minimal

        tableView.emptyDataSetDelegate = self
        tableView.emptyDataSetSource = self
        tableView.delegate = self
        tableView.dataSource = self
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.hidesBarsOnSwipe = false
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

    func hideKeyboard() {
        searchBar.resignFirstResponder()
    }

    func searchForColor(searchText: String) {
        colorManager.findColors(byQuery: searchText) { [weak self] (colors) in
            self?.colors = colors
            self?.setupView()
        }
    }

    func setupView() {
        self.tableView.reloadData()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        let cell = sender as! UITableViewCell
        let index = tableView.indexPath(for: cell)?.row ?? 0
        let vc = segue.destination as? ColorPageViewController
        vc?.colors = self.colors
        vc?.currentPosition = index
    }
}

class SearchBarContainerView: UIView {
    let searchBar: UISearchBar

    init(searchBar: UISearchBar) {
        self.searchBar = searchBar
        super.init(frame: CGRect())
        addSubview(searchBar)
    }

    override convenience init(frame: CGRect) {
        self.init(searchBar: UISearchBar())
        self.frame = frame
    }

    required init?(coder aDecoder: NSCoder) {
        self.searchBar = UISearchBar()
        super.init(coder: aDecoder)
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        searchBar.frame = bounds
    }
}

extension SearchTableViewController: UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return colors.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath as IndexPath) as! FavoriteCell
        let color = colors[indexPath.row]
        cell.color.backgroundColor = ColorUtils.getColorForHexCode(hex: color.hexCode)
        cell.name.text = color.colorDescription
        cell.brand.text = color.brand
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
    }
}

extension SearchTableViewController: DZNEmptyDataSetDelegate, DZNEmptyDataSetSource {
    func title(forEmptyDataSet scrollView: UIScrollView!) -> NSAttributedString! {
        let text = "No Search Results"
        let attrs = [NSAttributedString.Key.font: UIFont.boldSystemFont(ofSize: 18), NSAttributedString.Key.foregroundColor: UIColor.darkGray]
        return NSAttributedString(string: text, attributes: attrs)
    }
    
    func emptyDataSetShouldDisplay(_ scrollView: UIScrollView!) -> Bool {
        return colors.isEmpty
    }
}

extension SearchTableViewController: UISearchBarDelegate {
    func searchBarCancelButtonClicked(_ searchBar: UISearchBar) {
        hideKeyboard()
    }
    
    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        hideKeyboard()
    }
    
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        if searchBar.text != nil && !searchBar.text!.isEmpty {
            self.searchForColor(searchText: searchBar.text!)
        }
    }
}
