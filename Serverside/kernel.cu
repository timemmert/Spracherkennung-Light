
extern "C"
__global__ void mult(int *request,int *matrix,int *output)
{
	int index= threadIdx.x + blockIdx.x * blockDim.x;
	if(index<676*260)
	{
		//Durchiterieren tlength i d.h. auf 260 Blöcken
		//=> gridDim.x muss 260 sein
		int i=index / blockDim.x; //i =  index durch threadanzahl = wie oft schon durch
		int k=index-i*blockDim.x;
		//Durchiterieren qlength k mit jeweils 676 Threads
		if(matrix[k*260+i]==1&&request[k]==1) 
			output[i]++;
	}
	
	
}
