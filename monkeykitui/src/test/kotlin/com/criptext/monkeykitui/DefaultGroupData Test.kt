package com.criptext.monkeykitui

import com.criptext.monkeykitui.conversation.DefaultGroupData
import com.criptext.monkeykitui.recycler.MonkeyInfo
import com.criptext.monkeykitui.recycler.MonkeyItem
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should match`
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Created by hirobreak on 03/01/17.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, shadows = arrayOf(ShadowColor::class))
class `DefaultGroupData Test` {

    @Test
    fun `setMembers() successfully adds all members to hashmap`(){
        val groupData = DefaultGroupData("", "", null)

        val monkeyItem1 = createMonkeyInfoItem("abcde");

        val monkeyItem2 = createMonkeyInfoItem("xyz");

        groupData.setMembers(mutableListOf(monkeyItem1, monkeyItem2))

        groupData.users.get("abcde") `should be` monkeyItem1
        groupData.users.get("xyz") `should be` monkeyItem2
        groupData.users.size `should be` 2

    }

    @Test
    fun `removeMember() successfully removes a member by its id`(){
        val groupData = DefaultGroupData("", "", null)

        val monkeyItem1 = createMonkeyInfoItem("abcde");
        val monkeyItem2 = createMonkeyInfoItem("xyz");
        val monkeyItem3 = createMonkeyInfoItem("1234");
        val monkeyItem4 = createMonkeyInfoItem("paim");

        groupData.setMembers(mutableListOf(monkeyItem1, monkeyItem2, monkeyItem3, monkeyItem4))

        groupData.users.get("abcde") `should be` monkeyItem1
        groupData.users.get("xyz") `should be` monkeyItem2
        groupData.users.get("1234") `should be` monkeyItem3
        groupData.users.get("paim") `should be` monkeyItem4
        groupData.users.size `should be` 4

        groupData.removeMember("1234")

        groupData.users.get("abcde") `should be` monkeyItem1
        groupData.users.get("xyz") `should be` monkeyItem2
        groupData.users.get("paim") `should be` monkeyItem4
        groupData.users.size `should be` 3
    }

    @Test
    fun `addMember() successfully adds a member`(){
        val groupData = DefaultGroupData("", "", null)

        val monkeyItem1 = createMonkeyInfoItem("abcde");
        val monkeyItem2 = createMonkeyInfoItem("xyz");
        val monkeyItem3 = createMonkeyInfoItem("1234");

        groupData.setMembers(mutableListOf(monkeyItem1, monkeyItem2))

        groupData.users.get("abcde") `should be` monkeyItem1
        groupData.users.get("xyz") `should be` monkeyItem2
        groupData.users.size `should be` 2

        groupData.addMember(monkeyItem3)

        groupData.users.get("abcde") `should be` monkeyItem1
        groupData.users.get("xyz") `should be` monkeyItem2
        groupData.users.get("1234") `should be` monkeyItem3
        groupData.users.size `should be` 3
    }

    @Test
    fun `addMemberTyping() adds a member to the typing string and removeMemberTyping() remove them`(){
        val groupData = DefaultGroupData("", "", null)

        val monkeyItem1 = createMonkeyInfoItem("abcde");
        val monkeyItem2 = createMonkeyInfoItem("xyz");

        groupData.setMembers(mutableListOf(monkeyItem1, monkeyItem2))

        groupData.membersNameTyping `should match` ""

        groupData.addMemberTyping("abcde")

        groupData.membersNameTyping `should match` "abcde typing..."

        groupData.addMemberTyping("xyz")

        groupData.membersNameTyping `should match` "abcde, xyz typing..."

        groupData.removeMemberTyping("abcde");

        groupData.membersNameTyping `should match` "xyz typing..."

        groupData.removeMemberTyping("xyz");

        groupData.membersNameTyping `should match` ""

    }

    @Test
    fun `setMembersOnline() set the members online`(){
        val groupData = DefaultGroupData("", "", null)

        val monkeyItem1 = createMonkeyInfoItem("abcde");
        val monkeyItem2 = createMonkeyInfoItem("xyz");

        groupData.setMembers(mutableListOf(monkeyItem1, monkeyItem2))

        groupData.membersNameTyping `should match` ""

        groupData.membersOnline = "abcde,xyz"

        groupData.membersNameTyping `should match` "2 members online"

        groupData.membersOnline = "abcde"

        groupData.membersNameTyping `should match` "1 member online"

    }

    @Test
    fun `setInfoList() should build the correct and sorted arraylist`(){
        val groupData = DefaultGroupData("", "", null)

        val monkeyItem1 = createMonkeyInfoItem("abcde");
        val monkeyItem2 = createMonkeyInfoItem("xyz");
        val monkeyItem3 = createMonkeyInfoItem("1234");
        val monkeyItem4 = createMonkeyInfoItem("paim");

        groupData.setMembers(mutableListOf(monkeyItem1, monkeyItem2, monkeyItem3, monkeyItem4))
        groupData.membersOnline = "abcde,1234"
        groupData.admins = "xyz,paim"

        groupData.setInfoList("abcde", "pepinillo")

        groupData.infoList[0] `should be` monkeyItem3
        groupData.infoList[1].getSubtitle() `should match` "Online"
        groupData.infoList[1].getRightTitle() `should match` ""
        groupData.infoList[2].getTitle() `should match` "paim"
        groupData.infoList[2].getSubtitle() `should match` "Offline"
        groupData.infoList[3].getRightTitle() `should match` "Admin"
        groupData.infoList.size `should be` 4
    }

    fun createMonkeyInfoItem(identifier : String) : MonkeyInfo{
        return object: MonkeyInfo {
            var connection = ""
            var tag = ""
            override fun getInfoId() = identifier
            override fun getAvatarUrl() = ""
            override fun getTitle() = identifier
            override fun getSubtitle() = connection
            override fun getRightTitle() = tag
            override fun getColor() = 1
            override fun setRightTitle(rightTitle: String) {
                tag = rightTitle
            }
            override fun setSubtitle(rightTitle: String) {
                connection = rightTitle
            }
            override fun setColor(color: Int) {

            }
        }
    }



}