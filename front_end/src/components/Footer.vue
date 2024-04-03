<template>
    <footer id="footer">
    	<div class="container">
			<p class="copyright">Copyright ©2024 All rights reserved | This is the team project made <i class="fas fa-heart" aria-hidden="true"></i> by Kitri Security Development 7th.<br>
				<span class="highlight">[ Sehan Park, Miseong Kim, Hyunoh Park, Sihyun Oh, and Nayeong Joo ]</span></p>
        </div>
    </footer>
</template>

<style scoped>
* {
    font-family:  "Montserrat", Arial, sans-serif;
	font-weight: 400;
}
#footer {
    padding-top: 10vh; 
    padding-bottom: 3vh; 
	/* background: #f8f8f8; */
	padding: 2rem 0 2rem 0;
	width: 100%;
}
.copyright {
    color: #bbb;
    font-size: 0.9em;
    text-align: center;
    width: 100%;
    margin-top: 10px;
    margin-bottom: 10px;
    display: inline-block;
    list-style: none;
    border-left: 0;
    padding-left: 0;
}

.copyright .highlight {
    color: black;
}



@media (max-width: 888px) {
    #footer .row.double {
        display: flex;
        flex-direction: column;
		justify-content: center;
		align-items: center; /* 가운데 정렬 */
    }
    #footer .part {
        width: 100%;
        order: 1; /* .part를 먼저 배치 */
		justify-content: center;
		text-align: center; /* 텍스트 가운데 정렬 */
    }
    #footer .part2 {
        width: 100%;
        order: 2; /* .part2를 그 아래에 배치 */
		justify-content: center;
		text-align: center; /* 텍스트 가운데 정렬 */
    }
}
@media (max-width: 768px) {
	.right-part {
		margin-left: 40%;
	}
	.icons {
        display: flex;
        flex-wrap: wrap;
        justify-content: center;
    }
    .icons li {
        flex: 0 0 33%; /* 첫 번째 줄의 아이콘들 */
        max-width: 33%;
    }
    .icons li:nth-child(4),
    .icons li:nth-child(5) {
        flex: 0 0 50%; /* 두 번째 줄의 아이콘들 */
        max-width: 50%;
    }
}
@media (max-width: 565px) {
	.right-part {
		margin-left: 80%;
	}
}
</style>