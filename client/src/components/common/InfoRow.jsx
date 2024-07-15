const InfoRow = ({ label, value }) => (
  <div className='flex justify-between text-sm'>
    <span className='text-gray-600'>{label}</span>
    <span className='font-normal'>{value}</span>
  </div>
);

export default InfoRow;
