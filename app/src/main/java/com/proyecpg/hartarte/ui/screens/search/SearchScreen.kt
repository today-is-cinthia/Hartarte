package com.proyecpg.hartarte.ui.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.algolia.instantsearch.android.paging3.Paginator
import com.algolia.instantsearch.android.paging3.flow
import com.algolia.instantsearch.compose.filter.facet.FacetListState
import com.algolia.instantsearch.compose.item.StatsState
import com.algolia.instantsearch.compose.searchbox.SearchBoxState
import com.algolia.instantsearch.core.selectable.list.SelectableItem
import com.algolia.search.model.search.Facet
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.proyecpg.hartarte.data.product.Product
import com.proyecpg.hartarte.ui.components.ErrorItem
import com.proyecpg.hartarte.ui.components.LoadingItem
import com.proyecpg.hartarte.ui.components.Post
import com.proyecpg.hartarte.ui.components.SearchBar
import com.proyecpg.hartarte.ui.screens.PostSharedEvent
import com.proyecpg.hartarte.ui.theme.HartarteTheme
import com.proyecpg.hartarte.utils.QueryParams
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    searchBoxState: SearchBoxState,
    paginator: Paginator<Product>,
    statsText: StatsState<String>,
    stateLiked : HashMap<String, Boolean>,
    stateBookmarked : HashMap<String, Boolean>,
    onPostClick: (String) -> Unit,
    onPostSharedProcess: (PostSharedEvent) -> Unit,
    onReturn: () -> Unit
) {
    var isSearchOpened by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val pagingHits = paginator.flow.collectAsLazyPagingItems()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        topBar = {
            Column {
                SearchTopBar(
                    isOpened = isSearchOpened,
                    onSearchClick = {
                        isSearchOpened = !isSearchOpened
                    },
                    onReturn = onReturn
                )

                SearchBar(
                    searchBoxState = searchBoxState,
                    pagingHits = pagingHits,
                    listState = listState,
                    onValueChange = { scope.launch { listState.scrollToItem(0) } }
                )

                if (searchBoxState.query.isNotEmpty()) {
                    Stats(stats = statsText.stats)
                }

                SearchFilters(viewModel, isSearchOpened)

                Divider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }

    ) { innerPadding ->
        SearchScreenContent(innerPadding = innerPadding, viewModel = viewModel,
            onPostClick = onPostClick, onPostSharedProcess = onPostSharedProcess,
            stateLiked = stateLiked, stateBookmarked = stateBookmarked
        )
    }
}

@Composable
fun Stats(stats: String) {
    Text(
        modifier = Modifier.padding(start = 12.dp),
        text = stats,
        fontSize = 16.sp,
        maxLines = 1
    )
}

@Composable
fun FacetRow(
    modifier: Modifier = Modifier,
    selectableFacet: SelectableItem<Facet>
) {
    val (facet, isSelected) = selectableFacet
    Row(
        modifier = modifier.height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f)) {
            Text(
                modifier = Modifier.alignByBaseline(),
                text = facet.value,
                fontSize = 16.sp
            )
            Text(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .alignByBaseline(),
                text = facet.count.toString(),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
            )
        }
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
            )
        }
    }
}

@Composable
fun FacetList(
    modifier: Modifier = Modifier,
    facetList: FacetListState
) {
    Column(modifier) {
        Text(
            text = "Categories",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(14.dp)
        )
        LazyColumn(Modifier.background(MaterialTheme.colorScheme.background)) {
            items(facetList.items) { item ->
                FacetRow(
                    modifier = Modifier
                        .clickable { facetList.onSelection?.invoke(item.first) }
                        .padding(horizontal = 14.dp),
                    selectableFacet = item,
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .width(1.dp)
                )
            }
        }
    }
}

@Composable
fun SearchScreenContent(
    innerPadding: PaddingValues,
    viewModel: SearchViewModel,
    onPostClick: (String) -> Unit,
    onPostSharedProcess: (PostSharedEvent) -> Unit,
    stateLiked : HashMap<String, Boolean>,
    stateBookmarked : HashMap<String, Boolean>
){
    //Posts
    val postSearchState = viewModel.postSearchState.collectAsStateWithLifecycle()

    if (postSearchState.value == null ){
        return
    }

    val pagingPosts = postSearchState.value!!.collectAsLazyPagingItems()
    val refresh = pagingPosts.loadState.refresh
    val append = pagingPosts.loadState.append

    val state = rememberSwipeRefreshState(
        isRefreshing = pagingPosts.loadState.refresh is LoadState.Loading
    )

    SwipeRefresh(
        modifier = Modifier.fillMaxSize(),
        state = state,
        // use the provided LazyPagingItems.refresh() method,
        // no need for custom solutions
        onRefresh = { pagingPosts.refresh() }
    ) {
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            if (pagingPosts.loadState.refresh is LoadState.NotLoading) {
                items(items = pagingPosts){ post ->
                    post?.let{
                        val postId = it.postId?:""
                        val date = "10 de mayo del 2023, 10:23:11"
                        val username = it.user?.name ?: ""
                        val userPic =  it.user?.photo ?: ""
                        val title = it.titulo?:""
                        val description = it.descripcion?:""
                        val likeCount = it.likes?.toInt() ?: 0
                        val liked = stateLiked[postId]?:it.liked?:false
                        val bookmarked = stateBookmarked[postId]?:it.bookmarked?:false

                        it.images?.let { it1 ->
                            Post(
                                postId = postId,
                                images = it1.toList(),
                                username = username,
                                userPic = userPic,
                                title = title,
                                description = description,
                                isLiked = liked,
                                isBookmarked = bookmarked,
                                likesCount = likeCount,
                                onLike = { postId : String, like : Boolean ->
                                    viewModel.doLike(postId, like)
                                    onPostSharedProcess(PostSharedEvent.OnLiked(postId, like))
                                },
                                onBookmark = { postId : String, bookmark : Boolean ->
                                    viewModel.doBookmark(postId, bookmark)
                                    onPostSharedProcess(PostSharedEvent.OnBookmarked(postId, bookmark))
                                },
                                onPostClick = {
                                    onPostClick(postId)
                                }
                            )
                        }
                    }
                }
            }

            pagingPosts.loadState.apply {
                when {
                    refresh is LoadState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ){
                                LoadingItem()
                            }
                        }
                    }
                    refresh is Error -> {
                        item {
                            ErrorItem()
                        }
                    }
                    append is LoadState.Loading -> {
                        item {
                            LoadingItem()
                        }
                    }
                    append is Error -> {
                        item {
                            ErrorItem()
                        }
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    isOpened: Boolean,
    onSearchClick: () -> Unit,
    onReturn: () -> Unit
){
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Buscar",
                color = MaterialTheme.colorScheme.primary
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onReturn
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Return icon",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
            IconButton(
                onClick = onSearchClick
            ) {
                if (isOpened){
                    Icon(
                        imageVector = Icons.Default.ExpandLess,
                        contentDescription = "Expand less icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                else {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filters icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    )
}


@Composable
fun SearchFilters(
    viewModel: SearchViewModel,
    isOpened: Boolean
) {
    var isChecked by remember{ mutableStateOf("") }

    val icons = listOf(
        Icons.Default.ArrowUpward to "Más recientes",
        Icons.Default.Favorite to "Más gustados",
        Icons.Default.Bookmarks to "Más guardados"
    )

    AnimatedVisibility(
        visible = isOpened,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Spacer(modifier = Modifier.height(5.dp))
        
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            item {
                for ((icon, description) in icons){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        OutlinedIconToggleButton(
                            checked = isChecked == description,
                            onCheckedChange = {
                                isChecked = description

                                viewModel.onQueryChange(
                                    when (isChecked) {
                                        "Más recientes" -> QueryParams.MOST_RECENT
                                        "Más gustados" -> QueryParams.MOST_LIKED
                                        "Más guardados" -> QueryParams.MOST_BOOKMARKED
                                        else -> null
                                    }
                                )
                            }
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = description
                            )
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(text = description)
                    }

                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSearchFilters(){
    HartarteTheme {
        Box(modifier = Modifier.padding(all = 10.dp)){
            SearchFilters(
                viewModel = hiltViewModel(),
                isOpened = true
            )
        }
    }
}