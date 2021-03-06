package com.sxtx.user.fragment.main

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import com.like.base.adapter.rvhelper.DividerItemDecoration
import com.like.base.base.BaseFragment
import com.like.base.base.BaseStatusToolbarFragment
import com.lyh.protocol.data.PublicData
import com.sxtx.user.R
import com.sxtx.user.activity.VideoPlayActivity
import com.sxtx.user.adapter.SearchResultAdapter
import com.sxtx.user.model.bean.RecordBean
import com.sxtx.user.mvp.presenter.main.SearchResultPresenter
import com.sxtx.user.mvp.view.main.ISearchResultView
import kotlinx.android.synthetic.main.fragment_record.*

class SearchResultFragment : BaseStatusToolbarFragment<SearchResultPresenter>() , ISearchResultView {
    override fun error() {
        refresh_layout.finishRefresh()
    }

    //type 1-当前为收藏 2-当前为没有收藏
    override fun collection(position:Int,type: Int) {
        (adapter!!.data[position]).isCollected = type==1

         adapter!!.position=position
        adapter?.notifyItemChanged(position,(adapter!!.data[position]).isCollected)
    }



    private var  keyWords="";
    fun setKeyWords(mketWords:String){
        this.keyWords=mketWords;
    }


    var adapter: SearchResultAdapter? = null
    val list: MutableList<RecordBean> = arrayListOf()

    override fun getMainResId(): Int {
        return R.layout.fragment_search_result
    }

    override fun onInitView(savedInstanceState: Bundle?) {
        super.onInitView(savedInstanceState)
        onBack()
        setToolbarTitle("結果")
        showView(BaseFragment.MAIN_VIEW)


        val linearLayoutManager = LinearLayoutManager(activity)
        rv_record.layoutManager = linearLayoutManager
        adapter = SearchResultAdapter()
        rv_record.adapter = adapter
        rv_record.addItemDecoration(DividerItemDecoration(DividerItemDecoration.VERTICAL_LIST, R.drawable.listdivider_white_16))

    }

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)
        mPresenter.SureSearchKeyWordReqeust(keyWords)
    }


    override fun onInitData(savedInstanceState: Bundle?) {
        super.onInitData(savedInstanceState)



        adapter?.setOnItemChildClickListener { adapter, view, position ->
            val bean = adapter?.data!![position] as RecordBean
            when(view.id){

                R.id.ll_collection->{
                   mPresenter.ClickCollectReqeust(position,bean.videoId)

                }
            }
        }
        adapter?.setOnItemClickListener { adapter, view, position ->
            val bean = adapter?.data!![position] as RecordBean
//            start(VideoPlayFragment.newIncetance(bean.videoId))
            startActivity(Intent(activity, VideoPlayActivity::class.java).putExtra(VideoPlayActivity.KEY_ACTIVITY_VIDEO_ID,bean.videoId))
        }

        refresh_layout.setOnRefreshListener {
            mPresenter.SureSearchKeyWordReqeust(keyWords)
        }


    }





    override fun sureSearchList(videoList: List<PublicData.ClassifyVideoData>) {
        refresh_layout.finishRefresh()
        if (videoList.size>0){
            for (element in videoList){
                val recordBean = RecordBean()
                recordBean.setName(element.videoName)
                recordBean.setPic(element.pictureAddress)
                recordBean.setVideoId(element.videoId)
                recordBean.setCollected(element.isCollect==1)
                recordBean.setTime(element.videoTime)
                recordBean.setUptime(element.upTime)
                recordBean.setPlayNum(element.playNum)
                recordBean.setGiveMark(element.giveMark)
                this.list.add(recordBean!!)
            }
        }
        if (this.list.size == 0) {//没有数据
            adapter!!.setNewData(this.list)
            val emptyView = LayoutInflater.from(context).inflate(R.layout.empty_layout, null, false)
            adapter!!.emptyView = emptyView
        } else {//有数据
            adapter!!.setNewData(this.list)
        }
    }


}