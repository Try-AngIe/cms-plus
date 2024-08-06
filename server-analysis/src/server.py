import os
import inspect
import nbformat
import json

from .trigger import trigger

from nbconvert.preprocessors import ExecutePreprocessor
from pprint import pprint

from fastapi import FastAPI, Request, Body, Query
from fastapi.responses import JSONResponse
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field

# 기본 디렉토리 설정
BASE_DIR = os.path.dirname(os.path.abspath(inspect.getframeinfo(inspect.currentframe()).filename))
NOTEBOOKS_DIR = os.path.join(BASE_DIR, 'notebooks')

app = FastAPI()

# CORS 미들웨어 추가
app.add_middleware(
    CORSMiddleware,
    allow_origins=["https://www.hyosungcmsplus.site"],  # React 앱의 주소
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 모델 정의
class MemberData(BaseModel):
    enroll_year: int
    contract_duration: int
    total_contract_amount: int
    payment_type: str


# 루트 경로
@app.get("/")
async def read_root():
    return {"infra-test": "0802-6"}

# 노트북 실행 (GET 메서드)
@app.get("/notebook/{filepath:path}")
async def read_item(filepath, request: Request):
    filepath = os.path.join(NOTEBOOKS_DIR, filepath)
    ep = trigger(notebook_filename=filepath, params=request.query_params)
    
    cells = ep[0]['cells']
    all_outputs = []
    for i, cell in enumerate(cells):
        output = cell.get("outputs", [])
        if len(output) > 0:
            output_data = {
                "cell": i,
                "output": output
            }
            all_outputs.append(output_data)
    return {"filepath": filepath, "all_outputs": all_outputs}

@app.get("/notebook/{filepath:path}")
async def get_members(filepath: str, page: int = Query(1, ge=1), page_size: int = Query(10, ge=1, le=100)):
    full_filepath = os.path.join(NOTEBOOKS_DIR, filepath)
    
    # 노트북 파일 읽기
    with open(full_filepath, 'r', encoding='utf-8') as f:
        nb = nbformat.read(f, as_version=4)
    
    # ExecutePreprocessor 설정
    ep = ExecutePreprocessor(timeout=600, kernel_name='python3')
    
    # 노트북 실행을 위한 글로벌 네임스페이스 설정
    global_dict = {'page': page, 'page_size': page_size}
    
    # 노트북 실행
    ep.preprocess(nb, {'metadata': {'path': NOTEBOOKS_DIR}}, resources=global_dict)
    
    # get_member_list 함수 찾기 및 실행
    for cell in nb.cells:
        if cell.cell_type == 'code':
            if 'def get_member_list' in cell.source:
                exec(cell.source, global_dict)
                result = global_dict['get_member_list'](page, page_size)
                return json.dumps(result, indent=2)
    
    # get_member_list 함수를 찾지 못한 경우
    return JSONResponse(content={"error": "get_member_list function not found in the notebook"})

# 노트북 실행 (POST 메서드)
@app.post("/notebook/{filepath:path}")
async def execute_notebook(filepath: str, member_data: MemberData = Body(...)):
    full_filepath = os.path.join(NOTEBOOKS_DIR, filepath)
    
    # 노트북 파일 읽기
    with open(full_filepath, 'r', encoding='utf-8') as f:
        nb = nbformat.read(f, as_version=4)
    
    # ExecutePreprocessor 설정
    ep = ExecutePreprocessor(timeout=600, kernel_name='python3')
    
    # 노트북 실행을 위한 글로벌 네임스페이스 설정
    global_dict = {'member_data': member_data.dict()}
    
    # 노트북 실행
    ep.preprocess(nb, resources=global_dict)
    
    # execute_model 함수 찾기 및 실행
    for cell in nb.cells:
        if cell.cell_type == 'code':
            if 'execute_model' in cell.source:
                exec(cell.source, global_dict)
                result = global_dict['execute_model'](member_data.dict())
                return JSONResponse(content=result)
    
    # execute_model 함수를 찾지 못한 경우
    return JSONResponse(content={"error": "execute_model function not found in the notebook"})
