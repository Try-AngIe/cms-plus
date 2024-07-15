const Input = ({ label, required, className, ...props }) => (
  <label className={`block ${className}`}>
    <span
      className={`block text-sm font-medium text-slate-700 ${required ? "after:ml-0.5 after:text-red-500 after:content-['*']" : ''}`}>
      {label}
    </span>
    <input
      className='mt-1 w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm placeholder-slate-400 shadow-sm placeholder:text-sm focus:border-mint focus:outline-none focus:ring-1 focus:ring-mint sm:text-sm'
      {...props}
    />
  </label>
);

export default Input;
