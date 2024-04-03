<template>
    <footer id="footer">
    	<div class="container">
    		<div class="row double">
    			<div class="6u">
    				<div class="contact-contributor-row collapse-at-2">
    					<div class="left-part 6u">
    						<h3 class="contributors">Contributors</h3>
    						<ul class="alt"><li><a href="#">박세한 : </a></li>
    							<li><a href="#">김미성 : </a></li>
    							<li><a href="#">박현오 : </a></li>
    							<li><a href="#">오시현 : </a></li>
    							<li><a href="#">주나영 : </a></li>
    						</ul></div>
    					<div class="right-part 6u">
    						<h3 class="contact">Contact</h3>
    						<ul class="alt"><li><a href="#">kkuldong12@gmail.com</a></li>
    							<li><a href="#">abcde1234@gmail.com</a></li>
    							<li><a href="#">efgh5678@gmail.com</a></li>
    							<li><a href="#">lmno9012@gmail.com</a></li>
    							<li><a href="#">pqrs3456@gmail.com</a></li>
    						</ul>
                        </div>
    				</div>
    			</div>
    			<div class="part3 6u">
    				<h2>백엔드 개발자 프로젝트</h2>
    				<p>상기 서비스는 프로젝트 발표 및 포트폴리오를 목표로 하며 상업적인 용도로 이용될 소지가 없습니다. 또한 가져온 정보들은 모두 무료 오픈소스들을 이용하였습니다.</p>
    				<ul class="icons"><li><a href="#" class="icon fa-twitter"><span class="label">Twitter</span></a></li>
    					<li><a href="#" class="icon fa-facebook"><span class="label">Facebook</span></a></li>
    					<li><a href="#" class="icon fa-instagram"><span class="label">Instagram</span></a></li>
    					<li><a href="#" class="icon fa-linkedin"><span class="label">LinkedIn</span></a></li>
    					<li><a href="#" class="icon fa-pinterest"><span class="label">Pinterest</span></a></li>
    				</ul>
    			</div>
    		</div>
    	</div>
        <div class="copyright">
        	Made by: <a href="#">Kitri</a>
        </div>
    </footer>
</template>

<style scoped>
@font-face {
    font-family: 'Ownglyph_meetme-Rg';
    src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2402_1@1.0/Ownglyph_meetme-Rg.woff2') format('woff2');
    font-weight: normal;
    font-style: normal;
}
* {
    font-family: 'Ownglyph_meetme-Rg';
}
#footer {
    margin-top: 5%;
    padding-top: 10vh; /* 위쪽 padding 설정 */
    padding-bottom: 3vh; /* 아래쪽 padding 설정 */
	background: #f8f8f8;
	padding: 4em 0 6em 0;
	width: 100%;
}
#footer a {
	color: inherit;
}
.copyright {
	color: #bbb;
	font-size: 0.9em;
	padding: 0;
	text-align: center;
	width: 100%;
	margin-top: 10px;
	margin-bottom: 10px;
}
.copyright{
	display: inline-block;
	list-style: none;
	margin-left: 1.5em;
	padding-left: 1.5em;
	border-left: 0;
	margin-left: 0;
	padding-left: 0;
}
#footer .icons a {
	color: #bbb;
}
ul.icons {
	cursor: default;
	list-style: none;
	padding-left: 0;
}
.contact-contributor-row {
	display: flex;
	justify-content: space-evenly;
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