# [1]
# pip install requests -t python
# powershell Compress-Archive python requests.zip

# [2]
import json
import boto3
import base64
import requests
from datetime import datetime

def lambda_handler(event, context):
    
    #S3 이벤트에서 버킷과 객체 키를 가져옴
    bucket = event['Records'][0]['s3']['bucket']['name']
    key = event['Records'][0]['s3']['object']['key']
    
    #S3 클라이언트 생성
    #s3_client = boto3.client('s3')
    
    #현재 날짜와 시간
    now = datetime.now()
    current_time = now.strftime("%Y-%m-%d_%H-%M-%S")
    
    #로그 파일명
    log_file_name = f'BE_CD_LOG_{current_time}.log'
    
    #로그 내용
    log_content = f'백엔드 배포 로그'
    
    #GitHub 정보
    github_token = '****'
    github_user = 'rlatkd'
    github_org = 'Try-AngIe'
    github_repo = 'cms-plus'
    branch = 'dev'
    
    #커밋 메시지
    commit_message = f'release: 배포 완료'
    
    #GitHub API 엔드포인트
    url = f'https://api.github.com/repos/{github_org}/{github_repo}/contents/DEPLOY_LOG/server/{log_file_name}'
    
    #로그 내용 인코딩
    content_encoded = base64.b64encode(log_content.encode('utf-8')).decode('utf-8')
    
    #GitHub API 요청 데이터
    data = {
        'message': commit_message,
        'content': content_encoded,
        'branch': branch
    }
    
    #GitHub API 요청을 위한 requests 사용
    headers = {
        'Authorization': f'token {github_token}',
        'Content-Type': 'application/json',
        'Accept': 'application/vnd.github.v3+json'
    }
    
    response = requests.put(url, headers=headers, json=data)
    
    return {
        'statusCode': response.status_code,
        'body': json.dumps('로그 생성 완료')
    }
