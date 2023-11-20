// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC721/IERC721.sol";
import "@openzeppelin/contracts/token/ERC721/utils/ERC721Holder.sol";

contract NFTTransfer is ERC721Holder {
    address private _nftAddress;
    
    constructor(address nftAddress) {
        _nftAddress = nftAddress;
    }
    
    function transferNFT(address to, uint256 tokenId) external {
        IERC721 nft = IERC721(_nftAddress);
        nft.safeTransferFrom(msg.sender, to, tokenId);
    }
}
